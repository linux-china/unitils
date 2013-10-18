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
package org.unitils.core.util;


/**
 * Defines the contract for implementations that find a resource configuration on a test object, returning
 * a subtype of {@link ResourceConfig} that wraps the configuration
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 * @param <RC> Implementation of {@link ResourceConfig}
 */
public interface ResourceConfigLoader<RC extends ResourceConfig> {

	
	/**
	 * @param testObject The test instance, not null
	 * @return The resource configuration for the given test object. Null if the test object
	 * doesn't specify any configuration for the resource type in question
	 */
	RC loadResourceConfig(Object testObject);
}
