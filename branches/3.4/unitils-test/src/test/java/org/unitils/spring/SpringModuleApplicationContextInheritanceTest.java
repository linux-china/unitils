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
package org.unitils.spring;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.core.ConfigurationLoader;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * Test for ApplicationContext creation in a test class hierarchy for the {@link SpringModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModuleApplicationContextInheritanceTest {

    /* Tested object */
    SpringModule springModule;


    /**
     * Initializes the test and test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        springModule = new SpringModule();
        springModule.init(configuration);
    }


    /**
     * Tests creating the application context.
     * Both super and sub class have annotations with values and custom create methods.
     */
    @Test
    public void testCreateApplicationContext_overriden() {
        SpringTestCustomCreate springTest1 = new SpringTestCustomCreate();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTest1);

        assertNotNull(applicationContext);
        assertFalse(springTest1.createMethod1Called);
        assertTrue(springTest1.createMethod2Called);
    }


    /**
     * Tests creating the application context.
     * Both super and sub class have annotations with values and but only super class has custom create method.
     */
    @Test
    public void testCreateApplicationContext_overridenNoCustomCreateInSubClass() {
        SpringTestNoCustomCreate springTestNoCustomCreate = new SpringTestNoCustomCreate();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestNoCustomCreate);

        assertNotNull(applicationContext);
        assertTrue(springTestNoCustomCreate.createMethod1Called);
    }


    /**
     * Test creating an application context for 2 subclasses of the same superclass. The context of the
     * superclass (parent) should have been reused.
     */
    @Test
    public void testCreateApplicationContext_twice() {
        ApplicationContext applicationContext1 = springModule.getApplicationContext(new SpringTestNoCreation1());
        ApplicationContext applicationContext2 = springModule.getApplicationContext(new SpringTestNoCreation2());

        assertNotNull(applicationContext1);
        assertSame(applicationContext1, applicationContext2);
    }


    /**
     * Tests creating the application context. No context creation is done in the sub-class, the context of the super
     * class should be used.
     */
    @Test
    public void testCreateApplicationContext_onlyInSuperClass() {
        SpringTestNoCreation1 springTestNoCreation = new SpringTestNoCreation1();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestNoCreation);

        assertNotNull(applicationContext);
        assertTrue(springTestNoCreation.createMethod1Called);
    }


    /**
     * Test SpringTest super-class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestSuper {

        protected boolean createMethod1Called = false;

        @SpringApplicationContext
        protected ApplicationContext createMethod1(List<String> locations) {
            createMethod1Called = true;
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test Spring sub-class with custom create.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestCustomCreate extends SpringTestSuper {

        protected boolean createMethod2Called = false;

        @SpringApplicationContext
        protected ApplicationContext createMethod2(List<String> locations) {
            createMethod2Called = true;
            assertLenientEquals(asList("classpath:org/unitils/spring/services-config.xml"), locations);
            createMethod2Called = true;
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test Spring sub-class without custom create.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    public class SpringTestNoCustomCreate extends SpringTestSuper {
    }


    /**
     * Test SpringTest sub-class without any context declaration.
     */
    private class SpringTestNoCreation1 extends SpringTestSuper {
    }

    /**
     * Test SpringTest sub-class without any context declaration.
     */
    private class SpringTestNoCreation2 extends SpringTestSuper {
    }


}
