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
package org.unitils.dbmaintainer.version.impl;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import static java.util.Arrays.asList;
import java.util.List;
import static junit.framework.Assert.*;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dbmaintainer.version.impl.DefaultExecutedScriptInfoSource.PROPERTY_AUTO_CREATE_EXECUTED_SCRIPTS_TABLE;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_DATES;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import static org.unitils.util.CollectionUtils.asSet;
import org.unitils.util.PropertyUtils;

/**
 * Test class for {@link org.unitils.dbmaintainer.version.impl.DefaultExecutedScriptInfoSource}. The implementation is tested using a
 * test database. The dbms that is used depends on the database configuration in test/resources/unitils.properties
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultExecutedScriptInfoSourceTest extends UnitilsJUnit4 {

    /* The tested instance */
    DefaultExecutedScriptInfoSource dbVersionSource;

    /* The tested instance with auto-create configured */
    DefaultExecutedScriptInfoSource dbVersionSourceAutoCreate;

    /* The dataSource */
    @TestDataSource
    DataSource dataSource = null;

    /* The db support instance for the default schema */
    DbSupport defaultDbSupport;

    ExecutedScript executedScript1, executedScript2;

    private static String dialect = "h2";
    
    private List<String> schemas;
    /**
     * Initialize test fixture and creates a test version table.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        schemas = PropertyUtils.getStringList("database.schemaNames", configuration);
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        defaultDbSupport = getDefaultDbSupport(configuration, sqlHandler, dialect, schemas.get(0));

        configuration.setProperty(PROPERTY_AUTO_CREATE_EXECUTED_SCRIPTS_TABLE, "false");
        dbVersionSource = new DefaultExecutedScriptInfoSource();
        dbVersionSource.init(configuration, sqlHandler, dialect, schemas);

        configuration.setProperty(PROPERTY_AUTO_CREATE_EXECUTED_SCRIPTS_TABLE, "true");
        dbVersionSourceAutoCreate = new DefaultExecutedScriptInfoSource();
        dbVersionSourceAutoCreate.init(configuration, sqlHandler, dialect, schemas);

        dropExecutedScriptsTable();
        createExecutedScriptsTable();
    }

    @Before
    public void initTestData() throws ParseException {
        executedScript1 = new ExecutedScript(new Script("1_script1.sql", 10L, "xxx"),
                DateUtils.parseDate("20/05/2008 10:20:00", new String[]{"dd/MM/yyyy hh:mm:ss"}), true);
        executedScript2 = new ExecutedScript(new Script("script2.sql", 20L, "yyy"),
                DateUtils.parseDate("20/05/2008 10:25:00", new String[]{"dd/MM/yyyy hh:mm:ss"}), false);
    }


    /**
     * Cleanup by dropping the test version table.
     */
    @After
    public void tearDown() throws Exception {
        dropExecutedScriptsTable();
    }


    /**
     * Test setting and getting version
     */
    @Test
    public void testRegisterAndRetrieveExecutedScript() throws Exception {
        dbVersionSource.registerExecutedScript(executedScript1);
        assertLenientEquals(asList(executedScript1), dbVersionSource.getExecutedScripts());
        dbVersionSource.registerExecutedScript(executedScript2);
        assertLenientEquals(asList(executedScript1, executedScript2), dbVersionSource.getExecutedScripts());
    }


    /**
     * Tests getting the version, but no executed scripts table yet (e.g. first use)
     */
    @Test(expected = UnitilsException.class)
    public void testRegisterExecutedScript_NoExecutedScriptsTable() throws Exception {
        dropExecutedScriptsTable();

        dbVersionSource.registerExecutedScript(executedScript1);
    }


    /**
     * Tests getting the version, but no executed scripts table yet and auto-create is true.
     */
    @Test
    public void testGetDBVersion_noExecutedScriptsTableAutoCreate() throws Exception {
        dropExecutedScriptsTable();

        dbVersionSourceAutoCreate.registerExecutedScript(executedScript1);
        assertLenientEquals(asList(executedScript1), dbVersionSource.getExecutedScripts());
    }

    @Test
    public void testUpdateExecutedScript() {
        dbVersionSource.registerExecutedScript(executedScript1);
        executedScript1 = new ExecutedScript(executedScript1.getScript(), new Date(), false);
        dbVersionSource.updateExecutedScript(executedScript1);

        executedScript1.setSuccessful(true);
        assertReflectionEquals(asSet(executedScript1), dbVersionSource.getExecutedScripts(), LENIENT_ORDER, LENIENT_DATES);
    }

    @Test
    public void testClearAllRegisteredScripts() {
        dbVersionSource.registerExecutedScript(executedScript1);
        dbVersionSource.registerExecutedScript(executedScript2);
        dbVersionSource.clearAllExecutedScripts();
        assertEquals(0, dbVersionSource.getExecutedScripts().size());
    }

    @Test
    public void testIsFromScratchUpdateRecommended() throws SQLException {
        assertFalse(dbVersionSource.isFromScratchUpdateRecommended());
        assertFalse(dbVersionSourceAutoCreate.isFromScratchUpdateRecommended());

        dropExecutedScriptsTable();

        assertFalse(dbVersionSource.isFromScratchUpdateRecommended());
        assertTrue(dbVersionSourceAutoCreate.isFromScratchUpdateRecommended());
    }


    /**
     * Utility method to create the test version table.
     */
    private void createExecutedScriptsTable() throws SQLException {
        executeUpdate(dbVersionSource.getCreateExecutedScriptsTableStatement(), dataSource);
    }


    /**
     * Utility method to drop the test executed scripts table.
     */
    private void dropExecutedScriptsTable() throws SQLException {
        executeUpdateQuietly("drop table dbmaintain_scripts", dataSource);
    }

}
