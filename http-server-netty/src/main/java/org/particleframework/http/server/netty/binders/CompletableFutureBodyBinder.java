package org.particleframework.http.server.netty.binders;

import com.typesafe.netty.http.StreamedHttpRequest;
import org.particleframework.context.BeanLocator;
import org.particleframework.core.async.subscriber.CompletionAwareSubscriber;
import org.particleframework.core.convert.ArgumentConversionContext;
import org.particleframework.core.convert.ConversionService;
import org.particleframework.http.HttpRequest;
import org.particleframework.http.MediaType;
import org.particleframework.http.server.HttpServerConfiguration;
import org.particleframework.http.server.binding.binders.DefaultBodyAnnotationBinder;
import org.particleframework.http.server.binding.binders.NonBlockingBodyArgumentBinder;
import org.particleframework.http.server.netty.*;
import org.particleframework.http.server.netty.DefaultHttpContentProcessor;
import org.particleframework.http.server.netty.HttpContentProcessor;
import org.particleframework.core.type.Argument;
import org.particleframework.web.router.qualifier.ConsumesMediaTypeQualifier;
import org.reactivestreams.Subscription;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link NonBlockingBodyArgumentBinder} that handles {@link CompletableFuture} instances
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Singleton
public class CompletableFutureBodyBinder extends DefaultBodyAnnotationBinder<CompletableFuture>
        implements NonBlockingBodyArgumentBinder<CompletableFuture> {

    private final BeanLocator beanLocator;
    private final HttpServerConfiguration httpServerConfiguration;

    public CompletableFutureBodyBinder(BeanLocator beanLocator, HttpServerConfiguration httpServerConfiguration, ConversionService conversionService) {
        super(conversionService);
        this.beanLocator = beanLocator;
        this.httpServerConfiguration = httpServerConfiguration;
    }

    @Override
    public Argument<CompletableFuture> argumentType() {
        return Argument.of(CompletableFuture.class);
    }

    @Override
    public BindingResult<CompletableFuture> bind(ArgumentConversionContext<CompletableFuture> context, HttpRequest<?> source) {
        if (source instanceof NettyHttpRequest) {
            NettyHttpRequest nettyHttpRequest = (NettyHttpRequest) source;
            io.netty.handler.codec.http.HttpRequest nativeRequest = ((NettyHttpRequest) source).getNativeRequest();
            if (nativeRequest instanceof StreamedHttpRequest) {

                CompletableFuture future = new CompletableFuture();
                Optional<MediaType> contentType = source.getContentType();
                HttpContentProcessor<?> processor = contentType.flatMap(type ->
                        beanLocator.findBean(HttpContentSubscriberFactory.class,
                                new ConsumesMediaTypeQualifier<>(type))
                ).map(factory ->
                    factory.build(nettyHttpRequest)
                ).orElse(new DefaultHttpContentProcessor(nettyHttpRequest, httpServerConfiguration));

                processor.subscribe(new CompletionAwareSubscriber<Object>() {
                    @Override
                    protected void doOnSubscribe(Subscription subscription) {
                        subscription.request(1);
                    }

                    @Override
                    protected void doOnNext(Object message) {
                        nettyHttpRequest.setBody(message);
                        subscription.request(1);
                    }

                    @Override
                    protected void doOnError(Throwable t) {
                        future.completeExceptionally(t);
                    }

                    @Override
                    protected void doOnComplete() {
                        Optional<Argument<?>> firstTypeParameter = context.getFirstTypeVariable();
                        Optional body = nettyHttpRequest.getBody();
                        if(body.isPresent()) {

                            if (firstTypeParameter.isPresent()) {
                                Argument<?> arg = firstTypeParameter.get();
                                Class targetType = arg.getType();
                                Optional converted = conversionService.convert(body.get(), context.with(arg));
                                if (converted.isPresent()) {
                                    future.complete(converted.get());
                                } else {
                                    future.completeExceptionally(new IllegalArgumentException("Cannot bind JSON to argument type: " + targetType.getName()));
                                }
                            } else {
                                future.complete(body.get());
                            }
                        }
                        else {
                            future.complete(null);
                        }
                    }
                });

                return ()-> Optional.of(future);
            } else {

                return BindingResult.EMPTY;
            }
        } else {
            return BindingResult.EMPTY;
        }
    }
}
