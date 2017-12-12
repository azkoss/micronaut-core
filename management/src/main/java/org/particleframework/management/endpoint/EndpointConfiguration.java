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
package org.particleframework.management.endpoint;

import org.particleframework.context.annotation.Argument;
import org.particleframework.context.annotation.EachProperty;
import org.particleframework.core.util.Toggleable;

import java.util.Optional;

/**
 * An {@link Endpoint} configuration
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@EachProperty(value = "endpoints")
public class EndpointConfiguration implements Toggleable {

    protected String id;
    protected Optional<Boolean> enabled = Optional.empty();
    protected Optional<Boolean> sensitive = Optional.empty();

    private EndpointDefaultConfiguration defaultConfiguration;

    public EndpointConfiguration(@Argument String id, EndpointDefaultConfiguration defaultConfiguration) {
        this.id = id;
        this.defaultConfiguration = defaultConfiguration;
    }

    /**
     * @return The ID of the endpoint
     * @see Endpoint#value()
     */
    public String getId() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return enabled.orElse(defaultConfiguration.enabled);
    }

    /**
     * @return Does the endpoint expose sensitive information
     */
    public boolean isSensitive() {
        return sensitive.orElse(defaultConfiguration.sensitive);
    }

}
