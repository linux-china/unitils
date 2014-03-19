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
package org.unitils.reflectionassert.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.junit.After;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;

import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;

import org.unitils.database.DatabaseModule;
import org.unitils.database.annotations.Transactional;

import static org.unitils.database.util.TransactionMode.COMMIT;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;

import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparator;

import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;

import org.unitils.reflectionassert.difference.Difference;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;

import static java.util.Arrays.asList;

import java.util.Properties;


/**
 * Test class for Hibernate proxy related tests of the {@link ReflectionComparator} .
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Transactional(COMMIT)
public class ReflectionComparatorHibernateProxyTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ReflectionComparatorHibernateProxyTest.class);

    /* A test hibernate entity, with a link to a lazily loaded parent class */
    private Child testChild;

    protected DataSource dataSource;

    @HibernateSessionFactory("org/unitils/reflectionassert/hibernate/hibernate.cfg.xml")
    protected SessionFactory sessionFactory;

    /* Class under test */
    private ReflectionComparator reflectionComparator;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = initDatabaseModule();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }
        

        testChild = new Child(1L, new Parent(1L));
        testChild.getParent().setChildren(asList(testChild));

        reflectionComparator = createRefectionComparator();
        dropTestTables();
        createTestTables();
    }


    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     */
    @After
    public void tearDown() throws Exception {
        if (disabled) {
            return;
        }
        dropTestTables();
    }


    /**
     * Test comparing 2 values with the right one containing a Hibernate proxy.
     */
    @Test
    public void testGetDifference_rightProxy() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Child childWithParentProxy = (Child) sessionFactory.getCurrentSession().get(Child.class, 1L);
        Difference result = reflectionComparator.getDifference(testChild, childWithParentProxy);
        assertNull(result);
    }


    /**
     * Test comparing 2 values with the left one containing a Hibernate proxy.
     */
    @Test
    public void testGetDifference_leftProxy() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Child childWithParentProxy = (Child) sessionFactory.getCurrentSession().get(Child.class, 1L);
        Difference result = reflectionComparator.getDifference(childWithParentProxy, testChild);

        ReflectionAssert.assertLenientEquals(childWithParentProxy, testChild);
        assertNull(result);
    }


    /**
     * Test comparing 2 values with both containing a Hibernate proxy. The identifiers should have been used
     * to compare the proxy values.
     */
    @Test
    public void testGetDifference_bothProxy() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        // open 2 session two avoid the values being taken from the cache
        Child childWithParentProxy1 = (Child) sessionFactory.openSession().get(Child.class, 1L);
        Child childWithParentProxy2 = (Child) sessionFactory.openSession().get(Child.class, 1L);
        Difference result = reflectionComparator.getDifference(childWithParentProxy1, childWithParentProxy2);
        assertNull(result);
    }


    /**
     * Test comparing 2 values with both containing a different hibnerate proxy. The identifiers should have been used
     * to compare the proxy values.
     */
    @Test
    public void testGetDifference_bothProxyDifferentValue() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        // open 2 session two avoid the values being taken from the cache
        Child childWithParentProxy1 = (Child) sessionFactory.openSession().get(Child.class, 1L);
        Child childWithParentProxy2 = (Child) sessionFactory.openSession().get(Child.class, 2L);
        Difference result = reflectionComparator.getDifference(childWithParentProxy1, childWithParentProxy2);
        assertNotNull(result);
    }


    /**
     * Creates the test tables.
     */
    private void createTestTables() {
        executeUpdate("create table PARENT (id bigint not null, primary key (id))", dataSource);
        executeUpdate("create table CHILD (id bigint not null, parent_id bigint not null, primary key (id))", dataSource);
        executeUpdate("alter table CHILD add constraint CHILDTOPARENT foreign key (parent_id) references PARENT", dataSource);
        executeUpdate("insert into PARENT (id) values (1)", dataSource);
        executeUpdate("insert into PARENT (id) values (2)", dataSource);
        executeUpdate("insert into CHILD (id, parent_id) values (1, 1)", dataSource);
        executeUpdate("insert into CHILD (id, parent_id) values (2, 2)", dataSource);
    }


    /**
     * Removes the test tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table CHILD", dataSource);
        executeUpdateQuietly("drop table PARENT", dataSource);
    }
    
    private Properties initDatabaseModule() {
        Properties configuration = (Properties) new ConfigurationLoader().loadConfiguration().clone();
        configuration.setProperty("dbMaintainer.autoCreateExecutedScriptsTable", "false");
        configuration.setProperty("dbMaintainer.autoCreateDbMaintainScriptsTable", "false");
        configuration.setProperty("updateDataBaseSchema.enabled", "false");
        configuration.setProperty("database.userName", "sa");
        configuration.setProperty("database.password", "");
        configuration.setProperty("database.schemaNames", "public");
        configuration.setProperty("database.driverClassName", "org.h2.Driver");
        configuration.setProperty("database.url", "jdbc:h2:~/test");
        configuration.setProperty("database.dialect", "h2");
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        databaseModule.init(configuration);
        databaseModule.afterInit();
        
        dataSource = databaseModule.getWrapper("").getTransactionalDataSourceAndActivateTransactionIfNeeded(this);
        return configuration;
    }
}