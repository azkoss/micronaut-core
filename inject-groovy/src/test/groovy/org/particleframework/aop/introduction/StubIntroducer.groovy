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
package org.particleframework.aop.introduction

import org.particleframework.aop.MethodInterceptor
import org.particleframework.aop.MethodInvocationContext
import org.particleframework.core.convert.ConversionService
import org.particleframework.core.type.MutableArgumentValue

import javax.inject.Singleton

/**
 * @author Graeme Rocher
 * @since 1.0
 */
@Singleton
class StubIntroducer implements MethodInterceptor<Object,Object> {
    @Override
    Object intercept(MethodInvocationContext<Object, Object> context) {
        Iterator<MutableArgumentValue<?>> iterator = context.getParameters().values().iterator()
        if(iterator.hasNext())
            return iterator.next().getValue()
        return null

    }
}
