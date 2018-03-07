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
package io.micronaut.configuration.lettuce;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.EachProperty;

/**
 * Allows the configuration of multiple redis servers
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@EachProperty(value = RedisSetting.REDIS_SERVERS)
public class NamedRedisServersConfiguration extends AbstractRedisConfiguration {

    private final String serverName;

    public NamedRedisServersConfiguration(@Parameter String name) {
        this.serverName = name;
    }

    /**
     * @return The name of the server
     */
    public String getServerName() {
        return serverName;
    }
}
