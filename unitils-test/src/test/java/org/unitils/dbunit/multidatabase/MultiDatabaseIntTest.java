package org.unitils.dbunit.multidatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.SQLUnitils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.sqlassert.SqlAssert;
import org.unitils.dbunit.DbUnitModule;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.DataSets;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.annotation.ExpectedDataSets;


/**
 * Test Multiple Databases with DBUnit.
 *
 * @author Willemijn Wouters
 *
 * @since 3.4.1
 *
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class MultiDatabaseIntTest {
    private static final String DATABASE2 = "database2";
    private static final String DATABASE1 = "database1";

    @TestDataSource(DATABASE1)
    private DataSource dataSourceDatabase1;

    @TestDataSource(DATABASE2)
    private DataSource dataSourceDatabase2;

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException, IOException {
        Properties config = getCorrectProperties();

        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        databaseModule.init(config);
        databaseModule.afterInit();
        DbUnitModule dbunitModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DbUnitModule.class);
        dbunitModule.init(config);
        dbunitModule.afterInit();

       // SQLUnitils.executeUpdate("CREATE TABLE PERSON (PERSONID INT NOT NULL, PERSONNAME VARCHAR(20));", databaseModule.getWrapper(DATABASE2).getDataSource());
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    @DataSet(value = "MultiDatabaseIntTest.testOneDataSetDatabase1.xml",  databaseName=DATABASE1)
    public void testOneDataSetDatabase1() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 1L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 1L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Myrthe'", 0L, DATABASE1);
    }

    @Test
    @DataSet(value = "MultiDatabaseIntTest.testOneDataSetDatabase2.xml",  databaseName=DATABASE2)
    public void testOneDataSetDatabase2() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 1L, DATABASE2);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 0L, DATABASE2);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Myrthe'", 1L, DATABASE2);
    }

    @Test
    @DataSets(value= {@DataSet(value = "MultiDatabaseIntTest.testMultipleDataSetsDatabase1_1.xml", databaseName=DATABASE1), @DataSet(value = "MultiDatabaseIntTest.testMultipleDataSetsDatabase1_2.xml", databaseName=DATABASE1)})
    public void testMultipleDataSetsDatabase1() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 2L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 1L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Myrthe'", 0L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Maurits'", 1L, DATABASE1);
    }

    @Test
    @DataSets(value= {@DataSet(value = "MultiDatabaseIntTest.testMultipleDataSetsDatabase1_1.xml", databaseName=DATABASE1), @DataSet(value = "MultiDatabaseIntTest.testMultipleDataSetsDatabase1_2.xml", databaseName=DATABASE2)})
    public void testMultipleDataSetsMultipleDatabases() throws Exception {
        SqlAssert.assertCountSqlResult("select count(*) from person", 1L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person", 1L, DATABASE2);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 1L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Myrthe'", 0L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Maurits'", 0L, DATABASE1);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Maurits'", 1L, DATABASE2);
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 0L, DATABASE2);
    }

    @Test
    @ExpectedDataSets({@ExpectedDataSet(databaseName=DATABASE1, value = "MultiDatabaseIntTest.testMultipleExpectedDataSetsOnMultipleDatabases_1.xml"), @ExpectedDataSet(databaseName=DATABASE2, value= "MultiDatabaseIntTest.testMultipleExpectedDataSetsOnMultipleDatabases_2.xml")})
    public void testMultipleExpectedDataSetsOnMultipleDatabases() {
        SQLUnitils.executeUpdate("INSERT INTO person (personid, personname) values ('5', 'Andre');", dataSourceDatabase1);
        SQLUnitils.executeUpdate("INSERT INTO person (personid, personname) values ('6', 'Charles');", dataSourceDatabase1);
        SQLUnitils.executeUpdate("INSERT INTO person (personid, personname) values ('8', 'Luc');", dataSourceDatabase2);
        SQLUnitils.executeUpdate("INSERT INTO person (personid, personname) values ('9', 'Jean-Michel');", dataSourceDatabase2);


    }

    private static Properties getCorrectProperties() {
        Properties config = (Properties) Unitils.getInstance().getConfiguration().clone();
        config.setProperty("database.names", "database1, database2");
        config.setProperty("database.userName", "sa");
        config.setProperty("database.password", "");
        config.setProperty("database.schemaNames", "public");
        config.setProperty("database.driverClassName.database1", "org.h2.Driver");
        //config.setProperty("database.driverClassName.database1", "org.hsqldb.jdbcDriver");
        //config.setProperty("database.driverClassName.database2", "org.hsqldb.jdbcDriver");
        config.setProperty("database.driverClassName.database2", "org.h2.Driver");
        //config.setProperty("database.url.database1", "jdbc:hsqldb:mem:unitils1");
        config.setProperty("database.url.database1", "jdbc:h2:~/unitils1");
        //config.setProperty("database.url.database1", "jdbc:hsqldb:mem:unitils1");
        config.setProperty("database.url.database2", "jdbc:h2:~/unitils2");
        //config.setProperty("database.url.database2", "jdbc:h2:~/test");
        config.setProperty("database.dialect.database1", "h2");
        //config.setProperty("database.dialect.database1", "hsqldb");
        config.setProperty("database.dialect.database2", "h2");
        //config.setProperty("database.dialect.database2", "hsqldb");
        config.setProperty("database.dbMaintain.enabled", "true");
        config.setProperty("dbMaintainer.autoCreateExecutedScriptsTable", "true");
        config.setProperty("dbMaintainer.autoCreateDbMaintainScriptsTable", "true");
        config.setProperty("updateDataBaseSchema.enabled", "true");

        config.setProperty("dbMaintainer.updateSequences.enabled", "true");
        config.setProperty("dbMaintainer.keepRetryingAfterError.enabled","true");
        config.setProperty("org.unitils.dbmaintainer.script.ScriptSource.implClassName", "org.unitils.dbmaintainer.script.impl.DefaultScriptSource");
        config.setProperty("unitils.module.hibernate.enabled", "false");
        config.setProperty("unitils.module.jpa.enabled", "false");
        config.setProperty("unitils.module.spring.enabled", "false");
        config.setProperty("dbMaintainer.script.locations", "src/test/resources/dbscripts");
        config.setProperty("dbMaintainer.fromScratch.enabled", "true");
        return config;
    }

}
