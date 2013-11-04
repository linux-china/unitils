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
package org.unitils.orm.jpa.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;
import org.unitils.orm.common.util.OrmPersistenceUnitLoader;
import org.unitils.orm.jpa.JpaModule;
import org.unitils.util.ReflectionUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;

/**
 * Loads an <code>EntityManagerFactory</code> given a {@link JpaConfig} object
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JpaEntityManagerFactoryLoader implements OrmPersistenceUnitLoader<EntityManagerFactory, Object, JpaConfig> {


    public ConfiguredOrmPersistenceUnit<EntityManagerFactory, Object> getConfiguredOrmPersistenceUnit(Object testObject, JpaConfig entityManagerConfig) {
        AbstractEntityManagerFactoryBean factoryBean = createEntityManagerFactoryBean(testObject, entityManagerConfig);
        EntityManagerFactory entityManagerFactory = factoryBean.getObject();
        Object providerSpecificConfigurationObject = getJpaProviderSupport().getProviderSpecificConfigurationObject(factoryBean.getPersistenceProvider());
        return new ConfiguredOrmPersistenceUnit<EntityManagerFactory, Object>(entityManagerFactory, providerSpecificConfigurationObject);
    }


    /**
     * @param testObject The test instance, not null
     * @param jpaConfig  The configuration parameters for the <code>EntityManagerFactory</code>
     * @return A completely configured <code>AbstractEntityManagerFactoryBean</code>
     */
    protected AbstractEntityManagerFactoryBean createEntityManagerFactoryBean(Object testObject, JpaConfig jpaConfig) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(getDataSource());
        factoryBean.setJpaVendorAdapter(getJpaProviderSupport().getSpringJpaVendorAdaptor());
		String persistenceXmlFile = jpaConfig.getConfigFiles().iterator().next();
		if (!StringUtils.isEmpty(persistenceXmlFile)) {
			factoryBean.setPersistenceXmlLocation(persistenceXmlFile);
		}
        factoryBean.setPersistenceUnitName(jpaConfig.getPersistenceUnitName());
        LoadTimeWeaver loadTimeWeaver = getJpaProviderSupport().getLoadTimeWeaver();
        if (loadTimeWeaver != null) {
            factoryBean.setLoadTimeWeaver(loadTimeWeaver);
        }
        if (jpaConfig.getConfigMethod() != null) {
            try {
                ReflectionUtils.invokeMethod(testObject, jpaConfig
                        .getConfigMethod(), factoryBean);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Error while invoking custom config method", e.getCause());
            }
        }
        factoryBean.afterPropertiesSet();
        return factoryBean;
    }


    protected DataSource getDataSource() {
        return getDatabaseModule().getDefaultDataSourceWrapper().getDataSource();
    }


    protected JpaProviderSupport getJpaProviderSupport() {
        return getJpaModule().getJpaProviderSupport();
    }


    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }


    protected JpaModule getJpaModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(JpaModule.class);
    }
}
