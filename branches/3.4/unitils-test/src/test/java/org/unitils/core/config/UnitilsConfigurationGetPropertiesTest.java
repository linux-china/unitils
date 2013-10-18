/*
 * Copyright 2011, Unitils.org
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
package org.unitils.core.config;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfigurationGetPropertiesTest {

    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;


    @Test
    public void getProperties() {
        Properties properties = new Properties();
        unitilsConfiguration = new UnitilsConfiguration(properties);

        Properties result = unitilsConfiguration.getProperties();
        assertSame(properties, result);
    }
}
