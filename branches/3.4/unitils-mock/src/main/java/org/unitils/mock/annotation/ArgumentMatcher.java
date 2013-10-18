/*
 * Copyright 2008,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.mock.annotation;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * An annotation for indicating that a method is an argument matcher method. The annotated method
 * should register the argument matcher instance in the ArgumentMatcherRepository. Following example creates
 * an argument matcher implemented by the SameArgumentMatcher class and registers it in the repository: <p />
 * <code><pre>
 * &#064;ArgumentMatcher
 * public static &lt;T&gt; T same(T sameAs) {
 *      ArgumentMatcherRepository.registerArgumentMatcher(new SameArgumentMatcher(sameAs));
 *      return null;
 * }
 * </pre></code><p/>
 * By annotating the method with this annotation, it can now be used inline in a mock invocation to register the
 * argument matcher. For example:<p />
 * <code>
 * myMock.aMethod(1, "test", same(myObject));
 * </code>
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ArgumentMatcher {
}