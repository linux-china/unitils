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
package org.unitils.reflectionassert;

import junit.framework.TestCase;
import org.unitils.reflectionassert.difference.Difference;

import java.util.Calendar;

import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;


/**
 * Test class for {@link ReflectionComparator}. Contains tests with date types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorCalendarTest extends TestCase {

    /* Test object */
    private Calendar calendarA;

    /* Same as A but different instance */
    private Calendar calendarB;

    /* Calendar with a different value */
    private Calendar differentCalendar;


    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        calendarA = Calendar.getInstance();
        calendarA.set(2000, 11, 5);
        calendarB = Calendar.getInstance();
        calendarB.setTime(calendarA.getTime());
        differentCalendar = Calendar.getInstance();

        reflectionComparator = createRefectionComparator();
    }


    /**
     * Test for two equal dates.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(calendarA, calendarB);
        assertNull(result);
    }

    /**
     * Test for two different dates.
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(calendarA, differentCalendar);

        assertEquals(calendarA, result.getLeftValue());
        assertEquals(differentCalendar, result.getRightValue());
    }

}