/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils.mock.core.proxy;

import org.unitils.core.UnitilsException;

import static java.lang.System.arraycopy;
import static org.unitils.mock.core.proxy.ProxyUtils.isProxyClassName;
import static org.unitils.util.ReflectionUtils.getClassWithName;

/**
 * Class offering utilities involving the call stack
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class StackTraceUtils {


    /**
     * @param invokedClass The class for which an invocation can be found in the current call stack
     * @return the line nr of the invocation in that class, -1 if not found
     */
    public static int getInvocationLineNr(Class<?> invokedClass) {
        StackTraceElement[] invocationStackTrace = getInvocationStackTrace(invokedClass, false);
        if (invocationStackTrace == null) {
            return -1;
        }
        return invocationStackTrace[0].getLineNumber();
    }


    /**
     * @param invokedInterface Class/interface to which an invocation can be found in the current call stack
     * @return Stack trace that indicates the most recent method call in the stack that calls a method from the given class, null if not found
     */
    public static StackTraceElement[] getInvocationStackTrace(Class<?> invokedInterface) {
        return getInvocationStackTrace(invokedInterface, true);
    }


    public static StackTraceElement[] getInvocationStackTrace(Class<?> invokedInterface, boolean included) {
        StackTraceElement[] currentStackTrace = Thread.currentThread().getStackTrace();
        for (int i = currentStackTrace.length - 1; i >= 0; i--) {
            String className = currentStackTrace[i].getClassName();
            Class<?> clazz;
            try {
                clazz = getClassWithName(className);
            } catch (UnitilsException e) {
                // unable to load class, this should never happen for the class we are looking for
                continue;
            }
            if (invokedInterface.isAssignableFrom(clazz) || isProxyClassName(className)) {
                int index = included ? i : i + 1;
                return getStackTraceStartingFrom(currentStackTrace, index);
            }
        }
        return null;
    }


    public static StackTraceElement[] getStackTraceStartingFrom(StackTraceElement[] stackTraceElements, int index) {
        StackTraceElement[] result = new StackTraceElement[stackTraceElements.length - index];
        arraycopy(stackTraceElements, index, result, 0, stackTraceElements.length - index);
        return result;
    }

}
