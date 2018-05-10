/*
 * Copyright 2017-2018 original authors
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

package io.micronaut.management.endpoint;

import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Finds any sensitive endpoints.
 *
 * @author Sergio del Amo
 * @version 1.0
 */
@Singleton
public class EndpointSensitiveConfiguration implements ExecutableMethodProcessor<Endpoint> {

    private final EndpointConfiguration[] endpointConfigurations;
    private final EndpointDefaultConfiguration defaultConfiguration;
    private Map<Method, Boolean> endpointMethods = new HashMap<>();

    /**
     * Constructs with the existing and default endpoint configurations used to determine if a given endpoint is
     * sensitive.
     *
     * @param endpointConfigurations The endpoint configurations
     * @param defaultConfiguration   The default endpoint configuration
     */
    public EndpointSensitiveConfiguration(EndpointConfiguration[] endpointConfigurations,
                                          EndpointDefaultConfiguration defaultConfiguration) {
        this.endpointConfigurations = endpointConfigurations;
        this.defaultConfiguration = defaultConfiguration;
    }

    /**
     * @return Returns Map with the key being a method which identifies an {@link Endpoint} and a boolean value being
     * the sensitive configuration for the endpoint.
     */
    public Map<Method, Boolean> getEndpointMethods() {
        return endpointMethods;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        Optional<String> optionalId = beanDefinition.getValue(Endpoint.class, String.class);
        optionalId.ifPresent((id) -> {

            EndpointConfiguration configuration = Arrays
                .stream(endpointConfigurations)
                .filter((c) -> c.getId().equals(id))
                .findFirst()
                .orElseGet(() -> new EndpointConfiguration(id, defaultConfiguration));

            boolean sensitive = configuration
                .isSensitive()
                .orElseGet(() -> beanDefinition
                    .getValue(Endpoint.class, "defaultSensitive", Boolean.class)
                    .orElse(Endpoint.SENSITIVE));

            endpointMethods.put(method.getTargetMethod(), sensitive);
        });
    }
}
