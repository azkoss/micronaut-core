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

/**
 * Contains classes specific to CORS support within Particle
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Configuration
@Requires(property = "particle.server.cors.enabled", value = "true")
package org.particleframework.http.server.cors;

import org.particleframework.context.annotation.Configuration;
import org.particleframework.context.annotation.Requires;