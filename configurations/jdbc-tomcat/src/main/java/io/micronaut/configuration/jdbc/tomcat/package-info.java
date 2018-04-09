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
/**
 * Configuration for Tomcat JDBC data sources
 */
@Configuration
@Requires(classes = DataSource.class)
package io.micronaut.configuration.jdbc.tomcat;

import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
import org.apache.tomcat.jdbc.pool.DataSource;
import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;