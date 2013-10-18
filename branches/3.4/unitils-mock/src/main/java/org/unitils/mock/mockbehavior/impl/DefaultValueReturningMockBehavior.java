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
package org.unitils.mock.mockbehavior.impl;

import org.unitils.core.UnitilsException;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Mock behavior that returns a default value.
 * <p/>
 * Following defaults are used:
 * <ul>
 * <li>Number values: 0</li>
 * <li>Object values: null</li>
 * <li>Collections, arrays etc: empty values</li>
 * </ul>
 * <p/>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class DefaultValueReturningMockBehavior implements ValidatableMockBehavior {


    /**
     * Checks whether the mock behavior can be executed for the given invocation. An exception is raised if the method is a void method.
     *
     * @param proxyInvocation The proxy method invocation, not null
     */
    public void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException {
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (returnType == Void.TYPE) {
            throw new UnitilsException("Trying to define mock behavior that returns a value for a void method.");
        }
    }

    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return The default value
     */
    @SuppressWarnings("unchecked")
    public Object execute(ProxyInvocation proxyInvocation) {
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (returnType == Void.TYPE) {
            return null;
        }
        if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
            return false;
        }
        if (returnType.isPrimitive() || Number.class.isAssignableFrom(returnType)) {
            return resolveNumber(returnType);
        }
        if (List.class.equals(returnType)) {
            return new ArrayList();
        }
        if (Set.class.equals(returnType)) {
            return new TreeSet();
        }
        if (Map.class.equals(returnType)) {
            return new HashMap();
        }
        if (Collection.class.equals(returnType)) {
            return new ArrayList();
        }
        if (returnType.isArray()) {
            return Array.newInstance(returnType.getComponentType(), 0);
        }
        return null;
    }


    /**
     * Checking for the default java implementations of Number, this avoids class cast exceptions when using them
     *
     * @param numberType The number type, not null
     * @return The default value for that number type, e.g. 0F for floats
     */
    protected Number resolveNumber(Class<?> numberType) {
        if (Integer.class.equals(numberType) || Integer.TYPE.equals(numberType)) {
            return 0;
        }
        if (Short.class.equals(numberType) || Short.TYPE.equals(numberType)) {
            return (short) 0;
        }
        if (BigInteger.class.isAssignableFrom(numberType)) {
            return BigInteger.ZERO;
        }
        if (Long.class.equals(numberType) || Long.TYPE.equals(numberType)) {
            return 0l;
        }
        if (BigDecimal.class.isAssignableFrom(numberType)) {
            return BigDecimal.ZERO;
        }
        if (Double.class.equals(numberType) || Double.TYPE.equals(numberType)) {
            return 0d;
        }
        if (Byte.class.equals(numberType) || Byte.TYPE.equals(numberType)) {
            return (byte) 0;
        }
        if (Float.class.equals(numberType) || Float.TYPE.equals(numberType)) {
            return 0f;
        }
        return 0;
    }

}