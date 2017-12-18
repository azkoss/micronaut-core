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
package org.particleframework.cache.annotation;

import org.particleframework.cache.interceptor.CacheInterceptor;
import org.particleframework.cache.interceptor.DefaultCacheKeyGenerator;
import org.particleframework.cache.interceptor.CacheKeyGenerator;
import org.particleframework.context.annotation.AliasFor;
import org.particleframework.context.annotation.Type;

import java.lang.annotation.*;

/**
 * <p>An annotation that can be applied at the type or method level to indicate that the annotated operation should cause the
 * return value to be cached within the given cache name. Unlike {@link Cacheable} this annotation will never skip the original invocation.</p>
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@CacheConfig
@Type(CacheInterceptor.class)
@Repeatable(PutOperations.class)
public @interface CachePut {

    /**
     * Alias for {@link CacheConfig#cacheNames}.
     */
    @AliasFor(member = "cacheNames")
    String[] value() default {};

    /**
     * Alias for {@link CacheConfig#cacheNames}.
     */
    @AliasFor(annotation = CacheConfig.class, member = "cacheNames")
    String[] cacheNames() default {};


    /**
     * Alias for {@link CacheConfig#keyGenerator}.
     */
    @AliasFor(annotation = CacheConfig.class, member = "keyGenerator")
    Class<? extends CacheKeyGenerator> keyGenerator() default DefaultCacheKeyGenerator.class;

    /**
     * Limit the automatic {@link CacheKeyGenerator} to the given parameter names. Mutually exclusive with {@link #keyGenerator()}
     *
     * @return The parameter names that make up the key.
     */
    String[] parameters() default {};
    /**
     * <p>Whether the {@link CachePut} operation should be performed asynchronously and not block the returning value</p>
     *
     * <p>The value if <tt>async</tt> impacts behaviour in the following ways:</p>
     *
     * <ul>
     *     <li>For blocking return types when the value is <tt>false</tt> the method will not return until the value has been written and any cache write errors will be propagated back to the client.</li>
     *     <li>For blocking return types when the value is <tt>true</tt> the method will return prior to cache writes occurring and errors will be logged via the {@link org.particleframework.cache.AsyncCacheErrorHandler}.</li>*
     *     <li>When the return type is a {@link java.util.concurrent.CompletableFuture} and the value is <tt>false</tt> the future will
     * not complete until the {@link CachePut} operation is complete.</li>
     *     <li>When the return type is a {@link java.util.concurrent.CompletableFuture} and the value is <tt>true</tt> the future will complete prior to any {@link CachePut} operations completing and the operations will be executing asynchronously with errors logged by {@link org.particleframework.cache.AsyncCacheErrorHandler}.</li>
     * </ul>
     *
     *
      * @return True if cache writes should be done asynchronously
     */
    boolean async() default false;
}
