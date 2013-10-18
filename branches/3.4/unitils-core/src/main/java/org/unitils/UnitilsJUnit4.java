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
package org.unitils;

import org.junit.runner.RunWith;

/**
 * Base test class that will Unitils-enable your test. This base class will make sure that the
 * core unitils test listener methods are invoked in the expected order. See {@link org.unitils.core.TestListener} for
 * more information on the listener invocation order.
 * <p/>
 * This actually is an empty test class that only instructs JUnit4 to use a custom test runner for the test.
 * As an alternative to subclassing this class, you could also add the @RunWith(UnitilsJUnit4TestClassRunner.class) to
 * your test base class.
 *
 * @author Tim Ducheyne
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public abstract class UnitilsJUnit4 {


}
