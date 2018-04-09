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
package io.micronaut.web.router.resource;

import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.util.AntPathMatcher;
import io.micronaut.core.util.PathMatcher;

import javax.inject.Singleton;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Resolves resources from a set of resource loaders
 *
 * @author James Kleeh
 * @since 1.0
 */
@Singleton
public class StaticResourceResolver {

    private final String mapping;
    private final List<ResourceLoader> loaders;
    private final AntPathMatcher pathMatcher;

    StaticResourceResolver(StaticResourceConfiguration configuration) {
        this.loaders = configuration.getResourceLoaders();
        this.pathMatcher = PathMatcher.ANT;
        this.mapping = configuration.mapping;
    }

    public Optional<URL> resolve(String path) {
        if (!loaders.isEmpty() && pathMatcher.matches(mapping, path)) {
            path = pathMatcher.extractPathWithinPattern(mapping, path);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            for (ResourceLoader loader : loaders) {
                Optional<URL> resource = loader.getResource(path);
                if (resource.isPresent()) {
                    return resource;
                }
            }
        }
        return Optional.empty();
    }
}
