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
package org.particleframework.inject.annotation;

import org.particleframework.context.annotation.AliasFor;
import org.particleframework.context.annotation.DefaultScope;
import org.particleframework.core.annotation.AnnotationMetadata;
import org.particleframework.core.annotation.AnnotationUtil;
import org.particleframework.core.util.CollectionUtils;
import org.particleframework.core.value.OptionalValues;

import javax.inject.Scope;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.*;
import java.util.function.Consumer;

/**
 * An abstract implementation that builds {@link AnnotationMetadata}
 *
 * @param <T> The element type
 * @param <A> The annotation type
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public abstract class AbstractAnnotationMetadataBuilder<T, A> {

    /**
     * Build the meta data for the given element. If the element is a method the class metadata will be included
     *
     * @param element The element
     * @return The {@link AnnotationMetadata}
     */
    public AnnotationMetadata build(T element) {
        DefaultAnnotationMetadata annotationMetadata = new DefaultAnnotationMetadata();
        return buildInternal(null, element, annotationMetadata, true);
    }

    /**
     * Build the meta data for the given method element excluding any class metadata
     *
     * @param element The element
     * @return The {@link AnnotationMetadata}
     */
    public AnnotationMetadata buildForMethod(T element) {
        DefaultAnnotationMetadata annotationMetadata = new DefaultAnnotationMetadata();
        return buildInternal(null, element, annotationMetadata, false);
    }

    /**
     * Build the meta data for the given method element excluding any class metadata
     *
     * @param parent The parent element
     * @param element The element
     * @return The {@link AnnotationMetadata}
     */
    public AnnotationMetadata buildForParent(T parent, T element) {
        DefaultAnnotationMetadata annotationMetadata = new DefaultAnnotationMetadata();
        return buildInternal(parent, element, annotationMetadata, false);
    }
    /**
     * Get the type of the given annotation
     * @param annotationMirror The annotation
     * @return The type
     */
    protected abstract T getTypeForAnnotation(A annotationMirror);


    /**
     * Get the given type of the annotation
     *
     * @param annotationMirror The annotation
     * @return The type
     */
    protected abstract String getAnnotationTypeName(A annotationMirror);

    /**
     * Obtain the annotations for the given type
     * @param element The type element
     * @return The annotations
     */
    protected abstract List<? extends A> getAnnotationsForType(T element);

    /**
     * Build the type hierarchy for the given element
     *
     * @param element The element
     * @param inheritTypeAnnotations
     * @return The type hierarchy
     */
    protected abstract List<T> buildHierarchy(T element, boolean inheritTypeAnnotations);


    /**
     * Read the given member and value, applying conversions if necessary, and place the data in the given map
     * @param memberName The member
     * @param annotationValue The value
     * @param annotationValues The values to populate
     */
    protected abstract void readAnnotationValues(String memberName, Object annotationValue, Map<CharSequence, Object> annotationValues);

    /**
     * Read the raw annotation values from the given annoatation
     *
     * @param annotationMirror The annotation
     * @return The values
     */
    protected abstract Map<? extends T, ?> readAnnotationValues(A annotationMirror);

    /**
     * Resolve the annotations values from the given member for the given type
     * @param member The member
     * @param annotationType The type
     * @return The values
     */
    protected abstract OptionalValues<?> getAnnotationValues(T member, Class<?> annotationType);

    /**
     * Read the name of an annotation member
     * @param member The member
     * @return The name
     */
    protected abstract String getAnnotationMemberName(T member);

    protected AnnotationValue readNestedAnnotationValue(A annotationMirror) {
        AnnotationValue av;
        Map<? extends T, ?> annotationValues = readAnnotationValues(annotationMirror);
        if(annotationValues.isEmpty()) {
            av = new AnnotationValue(getAnnotationTypeName(annotationMirror));
        }
        else {

            Map<CharSequence, Object> resolvedValues = new LinkedHashMap<>();
            for (Map.Entry<? extends T, ?> entry : annotationValues.entrySet()) {
                T member = entry.getKey();
                OptionalValues<?> values = getAnnotationValues(member, AliasFor.class);
                Object annotationValue = entry.getValue();
                Optional<?> aliasMember = values.get("member");
                Optional<?> aliasAnnotation = values.get("annotation");
                if(aliasMember.isPresent() && !aliasAnnotation.isPresent()) {
                    String aliasedNamed = aliasMember.get().toString();
                    readAnnotationValues(aliasedNamed, annotationValue, resolvedValues);
                }
                String memberName = getAnnotationMemberName(member);
                readAnnotationValues(memberName, annotationValue, resolvedValues);
            }
            av = new AnnotationValue(getAnnotationTypeName(annotationMirror), resolvedValues);
        }

        return av;
    }

    /**
     * Populate the annotation data for the given annotation
     * @param annotationMirror The annotation
     * @param annotationData The annotation data
     */
    protected Map<CharSequence, Object> populateAnnotationData(A annotationMirror, Map<String, Map<CharSequence, Object>> annotationData, Map<String, Map<CharSequence, Object>> stereotypes) {
        String annotationName = getAnnotationTypeName(annotationMirror);
        Map<? extends T, ?> elementValues = readAnnotationValues(annotationMirror);
        if(CollectionUtils.isEmpty(elementValues)) {
            if(!annotationData.containsKey(annotationName)) {
                annotationData.put(annotationName, Collections.emptyMap());
            }
        }
        else {
            Map<CharSequence, Object> annotationValues = resolveAnnotationData(annotationData, annotationName, elementValues);
            for (Map.Entry<? extends T, ?> entry : elementValues.entrySet()) {
                T member = entry.getKey();
                OptionalValues<?> values = getAnnotationValues(member, AliasFor.class);
                Optional<?> aliasAnnotation = values.get("annotation");
                Object annotationValue = entry.getValue();
                if(aliasAnnotation.isPresent()) {
                    Optional<?> aliasMember = values.get("member");
                    if(aliasMember.isPresent()) {
                        Map<CharSequence, Object> data = resolveAnnotationData(stereotypes, aliasAnnotation.get().toString(), elementValues);
                        readAnnotationValues(aliasMember.get().toString(), annotationValue, data);
                    }
                }
                else {
                    Optional<?> aliasMember = values.get("member");
                    if(aliasMember.isPresent()) {
                        String aliasedNamed = aliasMember.get().toString();
                        readAnnotationValues(aliasedNamed, annotationValue, annotationValues);
                    }
                }
                readAnnotationValues(getAnnotationMemberName(member), annotationValue, annotationValues);
            }
            return annotationValues;
        }
        return Collections.emptyMap();
    }

    private AnnotationMetadata buildInternal(T parent, T element, DefaultAnnotationMetadata annotationMetadata, boolean inheritTypeAnnotations) {
        List<T> hierarchy = buildHierarchy(element, inheritTypeAnnotations);
        if(parent != null) {
            hierarchy.add(0, parent);
        }
        for (T currentElement : hierarchy) {
            List<? extends A> annotationHierarchy =
                    getAnnotationsForType(currentElement);

            if(annotationHierarchy.isEmpty()) continue;

            Map<String, Map<CharSequence, Object>> annotationValues = new LinkedHashMap<>();

            for (A a : annotationHierarchy) {
                String annotationName = getAnnotationTypeName(a);
                if(AnnotationUtil.INTERNAL_ANNOTATION_NAMES.contains(annotationName)) continue;

                Map<String, Map<CharSequence, Object>> stereotypes = new LinkedHashMap<>();
                List<A> stereotypeHierarchy = new ArrayList<>();
                buildStereotypeHierarchy(getTypeForAnnotation(a), stereotypeHierarchy);
                Collections.reverse(stereotypeHierarchy);
                for (A stereotype : stereotypeHierarchy) {
                    populateAnnotationData(stereotype, stereotypes, stereotypes);
                }

                Map<CharSequence, Object> values = populateAnnotationData(a, annotationValues, stereotypes);

                if(currentElement == element) {
                    annotationMetadata.addDeclaredAnnotation(annotationName, values);
                    if(!stereotypes.isEmpty()) {
                        for (Map.Entry<String, Map<CharSequence, Object>> stereotype : stereotypes.entrySet()) {
                            annotationMetadata.addDeclaredStereotype(annotationName, stereotype.getKey(), stereotype.getValue());
                        }
                    }
                }
                else {
                    annotationMetadata.addAnnotation(annotationName, values);
                    if(!stereotypes.isEmpty()) {
                        for (Map.Entry<String, Map<CharSequence, Object>> stereotype : stereotypes.entrySet()) {
                            annotationMetadata.addStereotype(annotationName, stereotype.getKey(), stereotype.getValue());
                        }
                    }
                }
            }

        }
        if(!annotationMetadata.hasDeclaredStereotype(Scope.class) && annotationMetadata.hasDeclaredStereotype(DefaultScope.class)) {
            Optional<String> value = annotationMetadata.getValue(DefaultScope.class, String.class);
            value.ifPresent(name -> annotationMetadata.addDeclaredAnnotation(name, Collections.emptyMap()));
        }
        return annotationMetadata;
    }

    private Map<CharSequence, Object> resolveAnnotationData(Map<String, Map<CharSequence, Object>> annotationData, String annotationName, Map<? extends T, ?> elementValues) {
        int size = elementValues.size();
        Map<CharSequence, Object> resolved = annotationData.computeIfAbsent(annotationName, s -> new LinkedHashMap<>(size));
        if(resolved.isEmpty() && size > 0) {
            resolved = new LinkedHashMap<>(size);
            annotationData.put(annotationName, resolved);
        }
        return resolved;
    }


    private void buildStereotypeHierarchy(T element, List<A> hierarchy) {
        List<? extends A> annotationMirrors = getAnnotationsForType(element);
        for (A annotationMirror : annotationMirrors) {

            String annotationName = getAnnotationTypeName(annotationMirror);
            if(!AnnotationUtil.INTERNAL_ANNOTATION_NAMES.contains(annotationName)) {
                T annotationType = getTypeForAnnotation(annotationMirror);
                hierarchy.add(annotationMirror);
                buildStereotypeHierarchy(annotationType, hierarchy);
            }
        }
    }


}
