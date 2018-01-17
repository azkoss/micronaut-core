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
package org.particleframework.web.router;

import org.particleframework.context.ExecutionHandleLocator;
import org.particleframework.context.processor.ExecutableMethodProcessor;
import org.particleframework.core.convert.ConversionService;
import org.particleframework.core.naming.conventions.MethodConvention;
import org.particleframework.core.naming.conventions.PropertyConvention;
import org.particleframework.http.HttpMethod;
import org.particleframework.http.MediaType;
import org.particleframework.core.type.Argument;
import org.particleframework.http.annotation.Produces;
import org.particleframework.inject.BeanDefinition;
import org.particleframework.inject.ExecutableMethod;
import org.particleframework.http.annotation.Controller;
import org.particleframework.http.annotation.HttpMethodMapping;
import org.particleframework.http.annotation.Consumes;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Optional;

/**
 * <p>This {@link RouteBuilder} will handle public methods of {@link Controller} instances that are mapped by convention</p>
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Singleton
public class AnnotatedControllerRouteBuilder extends DefaultRouteBuilder implements ExecutableMethodProcessor<Controller> {

    public AnnotatedControllerRouteBuilder(ExecutionHandleLocator executionHandleLocator, UriNamingStrategy uriNamingStrategy, ConversionService<?> conversionService) {
        super(executionHandleLocator, uriNamingStrategy, conversionService);
    }

    @Override
    public void process(BeanDefinition beanDefinition, ExecutableMethod method) {
        Class<?> declaringType = method.getDeclaringType();
        if (beanDefinition.hasStereotype(Controller.class) && !method.hasDeclaredStereotype(HttpMethodMapping.class)) {
            MediaType[] consumes = method.getValue(Consumes.class, MediaType[].class).orElse(null);
            MediaType[] produces = method.getValue(Produces.class, MediaType[].class).orElse(null);

            Class[] argumentTypes = method.getArgumentTypes();
            if (argumentTypes.length > 0 && Throwable.class.isAssignableFrom(argumentTypes[argumentTypes.length - 1])) {
                Class argumentType = argumentTypes[argumentTypes.length-1];
                ErrorRoute errorRoute = error(method.getDeclaringType(), argumentType, declaringType, method.getMethodName(), method.getArgumentTypes());
                errorRoute.consumes(consumes)
                          .produces(produces);
            }
            else {
                String annotationValue = beanDefinition.getValue(Controller.class, String.class).orElse("");
                String path;
                if(annotationValue.isEmpty()) {
                    path = getUriNamingStrategy().resolveUri(declaringType);
                }
                else {
                    if(!annotationValue.startsWith("/")) {
                        annotationValue = '/'+annotationValue;
                    }

                    path = annotationValue;
                }
                String methodName = method.getMethodName();
                Optional<MethodConvention> methodConvention = MethodConvention.forMethod(methodName);


                Optional<Argument> idArg = Arrays.stream(method.getArguments())
                        .filter((argument -> argument.getName()
                                .equals(PropertyConvention.ID.lowerCaseName())))
                        .findFirst();

                String id = idArg.map((arg) -> "{/id}").orElse("");
                methodConvention.ifPresent((convention) -> {
                    UriRoute uriRoute = buildRoute(HttpMethod.valueOf(convention.httpMethod()),
                            path + id,
                            declaringType,
                            methodName,
                            method.getArgumentTypes()
                    );
                    uriRoute.consumes(consumes)
                            .produces(produces);
                });

            }

        }
    }

}
