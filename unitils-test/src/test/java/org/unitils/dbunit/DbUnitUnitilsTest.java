/*
 * Copyright (c) Smals
 */
package org.unitils.dbunit;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.SQLUnitils;
import org.unitils.database.annotations.TestDataSource;


/**
 * test {@link DbUnitUnitils}.
 *
 * @author wiw
 *
 * @since
 *
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class DbUnitUnitilsTest {

    @TestDataSource
    private DataSource dataSource;

    @BeforeClass
    public static void setUpClass() {
        DatabaseModule databasemodule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        DataSource dataSource = databasemodule.getWrapper("").getDataSource();
        SQLUnitils.executeUpdate("CREATE TABLE fruit (id varchar(50),name varchar(50))", dataSource);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.unitils.dbunit.DbUnitUnitils#assertExpectedDataSet(java.io.File[])}.
     */
    @Test
    public void testAssertExpectedDataSetFileArray() throws IOException {
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(1, 'orange')", dataSource);
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(2, 'apple')", dataSource);

        DbUnitUnitils.assertExpectedDataSet(new File("src/test/java/org/unitils/dbunit/DbUnitUnitilsTest-testAssertExpectedDataSetFileArray.xml"));
    }

    @Test(expected = AssertionError.class)
    public void testAssertExpectedDataSetFileArray_otherValuesInDataset() throws IOException {
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(3, 'peach')", dataSource);
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(4, 'banana')", dataSource);

        DbUnitUnitils.assertExpectedDataSet(new File("src/test/java/org/unitils/dbunit/DbUnitUnitilsTest-testAssertExpectedDataSetFileArray.xml"));
    }


    /**
     * Test method for {@link org.unitils.dbunit.DbUnitUnitils#assertExpectedDataSet(java.lang.String[])}.
     */
    @Test
    public void testAssertExpectedDataSetStringArray() {
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(1, 'orange')", dataSource);
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(2, 'apple')", dataSource);

        DbUnitUnitils.assertExpectedDataSet("DbUnitUnitilsTest-testAssertExpectedDataSetFileArray.xml");
    }

    @Test(expected = AssertionError.class)
    public void testAssertExpectedDataSetStringArray_otherValuesInDataset() throws IOException {
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(3, 'peach')", dataSource);
        SQLUnitils.executeUpdate("insert into fruit (id, name) values(4, 'banana')", dataSource);

        DbUnitUnitils.assertExpectedDataSet("DbUnitUnitilsTest-testAssertExpectedDataSetFileArray.xml");
    }

    @After
    public void afterTest() {
        SQLUnitils.executeUpdate("delete from fruit", dataSource);
    }

    @AfterClass
    public static void afterClass() {
        DatabaseModule databasemodule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        DataSource dataSource = databasemodule.getWrapper("").getDataSource();
        SQLUnitils.executeUpdate("drop TABLE fruit", dataSource);
    }

}
