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
package org.particleframework.function.executor;

import org.particleframework.function.LocalFunctionRegistry;

/**
 * <p>Allows executing the function configured by {@link LocalFunctionRegistry#FUNCTION_NAME}</p>
 *
 * <p>Or a named function</p>
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface FunctionExecutor<I,O> {

    /**
     * Execute the function configured by {@link LocalFunctionRegistry#FUNCTION_NAME}
     *
     * @param input The input
     * @return The output
     */
    O execute(I input);

}
