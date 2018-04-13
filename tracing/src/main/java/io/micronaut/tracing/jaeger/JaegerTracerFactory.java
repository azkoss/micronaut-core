/*
 * Copyright 2018 original authors
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
package io.micronaut.tracing.jaeger;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;

/**
 * Registers a Jaeger tracer based on the jaeger configuration
 *
 * @author graemerocher
 * @since 1.0
 */
@Factory
@Requires(beans = JaegerConfiguration.class)
public class JaegerTracerFactory implements Closeable {

    private final JaegerConfiguration configuration;

    public JaegerTracerFactory(JaegerConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Adds a Jaeger based Open Tracing {@link Tracer}
     * @return The {@link Tracer}
     */
    @Singleton
    @Primary
    Tracer jaegerTracer() {
        Tracer tracer = configuration.getConfiguration().getTracer();
        if(!GlobalTracer.isRegistered()) {
            GlobalTracer.register(tracer);
        }
        return tracer;
    }

    @Override
    @PreDestroy
    public void close() throws IOException {
        configuration.getConfiguration().closeTracer();
    }

}