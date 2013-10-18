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
package org.unitils.mock.annotation;

import junit.framework.Assert;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DummyTest extends UnitilsJUnit4 {

    @Dummy
    protected JustAClass dummy;


    @Test
    public void testDummy() {
        Assert.assertNotNull(dummy);
        Assert.assertNotNull(dummy.getId());
        Assert.assertNotNull(dummy.getJustAClass());
    }


    @SuppressWarnings({"UnusedDeclaration"})
    protected class JustAClass {

        private Long id = 10L;

        private JustAClass justAClass;

        public Long getId() {
            return id;
        }

        public JustAClass getJustAClass() {
            return justAClass;
        }

    }
}
