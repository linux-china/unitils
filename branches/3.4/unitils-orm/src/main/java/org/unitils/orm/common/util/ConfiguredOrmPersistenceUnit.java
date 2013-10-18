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
package org.unitils.orm.common.util;

/**
 * Value object that wraps a persistence unit object and an implementation specific configuration object
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 * @param <ORMPU> Type of the persistence unit
 * @param <ORMCONFOBJ> Type of the configuration object
 */
public class ConfiguredOrmPersistenceUnit<ORMPU, ORMCONFOBJ> {

	/**
	 * The persistence unit
	 */
	private ORMPU ormPersistenceUnit;
	
	/**
	 * The implementation specific configuration object
	 */
	private ORMCONFOBJ ormConfigurationObject;


	/**
	 * Creates a new instance
	 * 
	 * @param ormPersistenceUnit
	 * @param ormConfigurationObject
	 */
	public ConfiguredOrmPersistenceUnit(ORMPU ormPersistenceUnit, ORMCONFOBJ ormConfigurationObject) {
		super();
		this.ormPersistenceUnit = ormPersistenceUnit;
		this.ormConfigurationObject = ormConfigurationObject;
	}

	
	public ORMPU getOrmPersistenceUnit() {
		return ormPersistenceUnit;
	}

	
	public ORMCONFOBJ getOrmConfigurationObject() {
		return ormConfigurationObject;
	}
	
}
