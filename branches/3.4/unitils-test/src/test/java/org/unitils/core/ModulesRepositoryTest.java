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
package org.unitils.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.DatabaseModule;

/**
 * Test for {@link ModulesRepositoryTest}.
 * 
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ModulesRepositoryTest extends UnitilsJUnit4 {


	/* A test module */
	private Module testModule1a = new TestModule1();

	/* Another test module */
	private Module testModule1b = new TestModule1();

	/* A test module with same type as testModule1b */
	private Module testModule2 = new TestModule2();

	/* Class under test */
	private ModulesRepository modulesRepository;


	/**
	 * Sets up the test fixture.
	 */
	@Before
	public void setUp() throws Exception {
		List<Module> modules = Arrays.asList(testModule1b, testModule1a, testModule2);
		modulesRepository = new ModulesRepository(modules);
	}


	/**
	 * Test initialisation of repository and creation of all test listeners for the modules.
	 */
	@Test
	public void testCreateListeners() {
		assertEquals(3, modulesRepository.getTestListeners().size());
		assertTrue(modulesRepository.getTestListener(testModule1a) instanceof TestModule1.TestListener1);
		assertTrue(modulesRepository.getTestListener(testModule1b) instanceof TestModule1.TestListener1);
		assertTrue(modulesRepository.getTestListener(testModule2) instanceof TestModule2.TestListener2);
	}


	/**
	 * Tests getting the first module of type TestModule1. Note: TestModule2 is a sub-type of TestModule1 and should be
	 * found first.
	 */
	@Test
	public void testGetModuleOfType_subType() {
		TestModule1 result = modulesRepository.getModuleOfType(TestModule2.class);
		assertLenientEquals(testModule2, result);
	}


	/**
	 * Tests getting the first module of type DatabaseModule, but none found.
	 */
	@Test
	public void testGetModuleOfType_noneFound() {
		try {
			modulesRepository.getModuleOfType(DatabaseModule.class);
			fail("A UnitilsException should have been thrown");
		} catch (UnitilsException e) {
			// Expected
		}
	}


	/**
	 * Tests getting the first module of type TestModule2
	 */
	@Test
	public void testGetModuleOfType_moreThanOneFound() {
		try {
			modulesRepository.getModuleOfType(TestModule1.class);
			fail("A UnitilsException should have been thrown");
		} catch (UnitilsException e) {
			// Expected
		}
	}


	/**
	 * Tests getting all modules of type TestModule2.
	 */
	@Test
	public void testGetModulesOfType() {
		List<TestModule1> result = modulesRepository.getModulesOfType(TestModule1.class);
		assertLenientEquals(Arrays.asList(testModule1a, testModule1b, testModule2), result);
	}


	/**
	 * Tests getting all modules of type TestModule1. Note: TestModule2 is a sub-type of TestModule1 and should also be
	 * found.
	 */
	@Test
	public void testGetModulesOfType_subType() {
		List<TestModule2> result = modulesRepository.getModulesOfType(TestModule2.class);
		assertLenientEquals(Arrays.asList(testModule2), result);
	}


	/**
	 * Tests getting all module of type DatabaseModule, but none found. An empty list should be returned.
	 */
	@Test
	public void testGetModulesOfType_noneFound() {
		List<DatabaseModule> result = modulesRepository.getModulesOfType(DatabaseModule.class);
		assertTrue(result.isEmpty());
	}


	/**
	 * A test module, creating its own test listener.
	 */
	private static class TestModule1 implements Module {

		public void init(Properties configuration) {
		}

		public void afterInit() {
		}

		public TestListener getTestListener() {
			return new TestListener1();
		}

		public DatabaseModule getDatabaseModule() {
            return null;
        }

        public static class TestListener1 extends TestListener {
		}
	}


	/**
	 * A test module that is a subtype of TestModule1 and also creates its own test listener.
	 */
	private static class TestModule2 extends TestModule1 implements Module {

		@Override
		public TestListener getTestListener() {
			return new TestListener2();
		}

		public DatabaseModule getDatabaseModule() {
            return null;
        }

        public static class TestListener2 extends TestListener {
		}
	}


}
