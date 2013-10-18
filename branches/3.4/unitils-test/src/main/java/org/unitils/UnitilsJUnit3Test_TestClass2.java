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

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

/**
 * JUnit 3 test class containing 2 test methods. This test test-class is used in the
 * {@link JUnitUnitilsInvocationTest} tests.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsJUnit3Test_TestClass2 extends UnitilsJUnit3TestBase {

	public void test1() {
        registerTestInvocation(TEST_METHOD, "test1");
    }


    public void test2() {
        registerTestInvocation(TEST_METHOD, "test2");
    }
	
}

