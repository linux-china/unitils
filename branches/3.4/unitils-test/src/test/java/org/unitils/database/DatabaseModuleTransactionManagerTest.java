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
package org.unitils.database;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.unitils.database.util.TransactionMode.COMMIT;
import static org.unitils.database.util.TransactionMode.DISABLED;
import static org.unitils.database.util.TransactionMode.ROLLBACK;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.Transactional;

/**
 * Tests verifying whether the SimpleTransactionManager functions correctly.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleTransactionManagerTest extends DatabaseModuleTransactionalTestBase {

    private DatabaseModule databaseModule;

    private TransactionsDisabledTest transactionsDisabledTest;

    private RollbackTest rollbackTest;

    private CommitTest commitTest;
    
    private DataSourceWrapper wrapper;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        initializeDatabaseModule();

        transactionsDisabledTest = new TransactionsDisabledTest();
        rollbackTest = new RollbackTest();
        commitTest = new CommitTest();
    }


    /**
     * Cleans up test by resetting the unitils instance.
     */
    @After
    public void tearDown() throws Exception {
        Unitils.getInstance().init();
    }


    /**
     * Tests for a test with transactions disabled
     */
    @Test
    public void testWithTransactionsDisabled() throws Exception {
        mockConnection1.close();
        mockConnection2.close();
        replay(mockConnection1, mockConnection2);

        Method testMethod = TransactionsDisabledTest.class.getMethod("test", new Class[]{});
        databaseModule.startTransactionForTestMethod(transactionsDisabledTest, testMethod);
        Connection conn1 = wrapper.getDataSourceAndActivateTransactionIfNeeded().getConnection();
        conn1.close();
        Connection conn2 = wrapper.getDataSourceAndActivateTransactionIfNeeded().getConnection();
        conn2.close();
        assertNotSame(conn1, conn2);
        databaseModule.endTransactionForTestMethod(transactionsDisabledTest, testMethod);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test with transaction rollback configured
     */
    @Ignore
    @Test
    public void testRollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        Method testMethod = RollbackTest.class.getMethod("test", new Class[]{});
        DataSource dataSource = wrapper.getTransactionalDataSourceAndActivateTransactionIfNeeded(rollbackTest);
        databaseModule.startTransactionForTestMethod(rollbackTest, testMethod);
        Connection connection1 = dataSource.getConnection();
        Connection targetConnection1 = ((ConnectionProxy) connection1).getTargetConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        Connection targetConnection2 = ((ConnectionProxy) connection2).getTargetConnection();
        connection2.close();
        assertSame(targetConnection1, targetConnection2);
        databaseModule.endTransactionForTestMethod(rollbackTest, testMethod);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test with transaction commit configured
     */
    @Ignore
    @Test
    public void testCommit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        Method testMethod = CommitTest.class.getMethod("test", new Class[]{});
        DataSource dataSource = wrapper.getTransactionalDataSourceAndActivateTransactionIfNeeded(commitTest);
        databaseModule.startTransactionForTestMethod(commitTest, testMethod);
        Connection connection1 = dataSource.getConnection();
        Connection targetConnection1 = ((ConnectionProxy) connection1).getTargetConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        Connection targetConnection2 = ((ConnectionProxy) connection2).getTargetConnection();
        connection2.close();
        assertSame(targetConnection1, targetConnection2);
        databaseModule.endTransactionForTestMethod(commitTest, testMethod);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Class that plays the role of a unit test, with transactions disabled
     */
    @Transactional(DISABLED)
    public static class TransactionsDisabledTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with transaction rollback enabled (=default, so no
     *
     * @Transactional annotation required
     */
    @Transactional(ROLLBACK)
    public static class RollbackTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with transaction commit enabled
     */
    @Transactional(COMMIT)
    public static class CommitTest {

        public void test() {
        }
    }
    
    private void initializeDatabaseModule() {
        configuration.setProperty("unitils.module.spring.enabled", "true");
        configuration.setProperty("updateDataBaseSchema.enabled", "true");
        configuration.setProperty("dbMaintainer.autoCreateExecutedScriptsTable", "false");
        configuration.setProperty("dbMaintainer.autoCreateDbMaintainScriptsTable", "true");
        configuration.setProperty("updateDataBaseSchema.enabled", "false");
        configuration.setProperty("dbMaintainer.generateDataSetStructure.enabled","false");
        databaseModule = getDatabaseModule();
        databaseModule.init(configuration);
        databaseModule.afterInit();
        wrapper = databaseModule.getWrapper("");
        databaseModule.setWrapper(wrapper);
        
        databaseModule.getTransactionManager();
        databaseModule.registerTransactionManagementConfiguration();
    }

}
