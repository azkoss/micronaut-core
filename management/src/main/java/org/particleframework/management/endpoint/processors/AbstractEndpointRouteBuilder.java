/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.management.endpoint.processors;

import org.particleframework.context.ApplicationContext;
import org.particleframework.core.convert.ConversionService;
import org.particleframework.core.naming.NameUtils;
import org.particleframework.core.type.Argument;
import org.particleframework.core.async.subscriber.Completable;
import org.particleframework.http.annotation.Parameter;
import org.particleframework.http.uri.UriTemplate;
import org.particleframework.inject.BeanDefinition;
import org.particleframework.inject.ExecutableMethod;
import org.particleframework.inject.qualifiers.Qualifiers;
import org.particleframework.management.endpoint.Endpoint;
import org.particleframework.management.endpoint.EndpointConfiguration;
import org.particleframework.management.endpoint.EndpointDefaultConfiguration;
import org.particleframework.web.router.DefaultRouteBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Abstract {@link org.particleframework.web.router.RouteBuilder} implementation for {@link Endpoint} method processors
 *
 * @author Graeme Rocher
 * @since 1.0
 */
class AbstractEndpointRouteBuilder extends DefaultRouteBuilder implements Completable  {
    private static final Pattern ENDPOINT_ID_PATTERN = Pattern.compile("\\w+");

    private Map<Class, Optional<EndpointConfiguration>> endpointIds = new ConcurrentHashMap<>();
    private final ApplicationContext beanContext;

    AbstractEndpointRouteBuilder(ApplicationContext applicationContext, UriNamingStrategy uriNamingStrategy, ConversionService<?> conversionService) {
        super(applicationContext, uriNamingStrategy, conversionService);
        this.beanContext = applicationContext;
    }

    protected Optional<EndpointConfiguration> resolveActiveEndPointId(Class<?> declaringType) {
        return endpointIds.computeIfAbsent(declaringType, aClass -> {
            Optional<? extends BeanDefinition<?>> opt = beanContext.findBeanDefinition(declaringType);
            if (opt.isPresent()) {
                BeanDefinition<?> beanDefinition = opt.get();
                if (beanDefinition.hasStereotype(Endpoint.class)) {
                    String id = beanDefinition.getValue(Endpoint.class, String.class).orElse(null);
                    if (id == null || !ENDPOINT_ID_PATTERN.matcher(id).matches()) {
                        id = NameUtils.hyphenate( beanDefinition.getName() );
                    }

                    return findEndpointConfiguration(id);
                }
            }

            return Optional.empty();
        });
    }

    private Optional<EndpointConfiguration> findEndpointConfiguration(String id) {
        Optional<EndpointConfiguration> config = beanContext.findBean(EndpointConfiguration.class, Qualifiers.byName(id));
        if(config.isPresent()) {
            EndpointConfiguration endpointConfiguration = config.get();
            if(endpointConfiguration.isEnabled()) {
                return Optional.of(endpointConfiguration);
            }
        }
        else {
            Optional<EndpointConfiguration> allConfig = beanContext.findBean(EndpointConfiguration.class, Qualifiers.byName("all"));
            if(allConfig.isPresent()) {
                EndpointConfiguration c = allConfig.get();
                if(c.isEnabled()) {
                    return Optional.of(c);
                }
            }
            else {
                return Optional.of(new EndpointConfiguration(id, new EndpointDefaultConfiguration()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final void onComplete() {
        endpointIds.clear();
    }

    protected UriTemplate buildUriTemplate(ExecutableMethod<?, ?> method, String id) {
        UriTemplate template = new UriTemplate(uriNamingStrategy.resolveUri(id));
        for (Argument argument : method.getArguments()) {
            if(isPathParameter(argument)) {
                template = template.nest("/{" + argument.getName() + "}");
            }
        }
        return template;
    }

    protected boolean isPathParameter(Argument argument) {
        return argument.getAnnotations().length == 0 || argument.getAnnotation(Parameter.class) != null;
    }
}
