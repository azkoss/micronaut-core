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
package io.micronaut.inject.writer;

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.configuration.ConfigurationMetadataBuilder;
import org.objectweb.asm.Type;

import javax.annotation.concurrent.Immutable;

/**
 * Stores data to be used when visiting a configuration builder method
 *
 * @author James Kleeh
 * @author Graeme Rocher
 * @since 1.0
 */
@Immutable
@Internal
class ConfigBuilderState {

    private final String name;
    private final Type type;
    private final boolean invokeMethod;
    private final ConfigurationMetadataBuilder metadataBuilder;
    private final AnnotationMetadata annotationMetadata;

    /**
     * Constructs a config builder
     *
     * @param type               The builder type
     * @param name               The name of the field or method
     * @param isMethod           Is the configuration builder resolver a method
     * @param annotationMetadata The annotation metadata
     * @param metadataBuilder    The metadata builder
     */
    ConfigBuilderState(Object type, String name, boolean isMethod, AnnotationMetadata annotationMetadata, ConfigurationMetadataBuilder metadataBuilder) {
        this.type = AbstractClassFileWriter.getTypeReference(type);
        this.name = name;
        this.invokeMethod = isMethod;
        this.metadataBuilder = metadataBuilder;
        this.annotationMetadata = annotationMetadata;
    }

    public ConfigurationMetadataBuilder<?> getMetadataBuilder() {
        return metadataBuilder;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isMethod() {
        return invokeMethod;
    }

    public AnnotationMetadata getAnnotationMetadata() {
        return annotationMetadata;
    }
}
