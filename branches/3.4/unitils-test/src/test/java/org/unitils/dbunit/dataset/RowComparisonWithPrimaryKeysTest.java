/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dbunit.dataset;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;

/**
 * Tests the comparison behavior of a data set row using primary keys.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparisonWithPrimaryKeysTest extends UnitilsJUnit4 {

    private Row expectedRow = new Row();
    private Row actualRow = new Row();


    @Test
    public void testEqualPrimaryKey() throws Exception {
        addColumn(expectedRow, "pk1", "value1");
        addColumn(expectedRow, "pk2", "value2");
        addPrimaryKeyColumn(actualRow, "pk1", "value1");
        addPrimaryKeyColumn(actualRow, "pk2", "value2");

        boolean result = expectedRow.hasDifferentPrimaryKeyColumns(actualRow);

        assertFalse(result);
    }


    @Test
    public void testEqualPrimaryKeyButOnlyPartInExpected() throws Exception {
        addColumn(expectedRow, "pk1", "value1");
        addPrimaryKeyColumn(actualRow, "pk1", "value1");
        addPrimaryKeyColumn(actualRow, "pk2", "value2");

        boolean result = expectedRow.hasDifferentPrimaryKeyColumns(actualRow);

        assertFalse(result);
    }


    @Test
    public void testDifferentPrimaryKey() throws Exception {
        addColumn(expectedRow, "pk1", "value1");
        addColumn(expectedRow, "pk2", "value1");
        addPrimaryKeyColumn(actualRow, "pk1", "value1");
        addPrimaryKeyColumn(actualRow, "pk2", "yyyy");

        boolean result = expectedRow.hasDifferentPrimaryKeyColumns(actualRow);

        assertTrue(result);
    }


    @Test
    public void testEqualRows() throws Exception {
        addColumn(expectedRow, "pk", "value1");
        addColumn(expectedRow, "column", "value2");
        addPrimaryKeyColumn(actualRow, "pk", "value1");
        addColumn(actualRow, "column", "value2");

        RowDifference result = expectedRow.compare(actualRow);

        assertNull(result);
    }


    @Test
    public void testDifferentValues() throws Exception {
        addColumn(expectedRow, "pk", "value1");
        addColumn(expectedRow, "column", "value2");
        addPrimaryKeyColumn(actualRow, "pk", "xxxx");
        addColumn(actualRow, "column", "yyyy");

        RowDifference result = expectedRow.compare(actualRow);

        assertColumnDifference(result, "pk", "value1", "xxxx");
        assertColumnDifference(result, "column", "value2", "yyyy");
    }


    @Test
    public void testMissingColumns() throws Exception {
        addPrimaryKeyColumn(expectedRow, "pk", "value1");
        addColumn(expectedRow, "column", "value2");

        RowDifference result = expectedRow.compare(actualRow);

        assertMissingColumn(result, "pk");
        assertMissingColumn(result, "column");
    }


    @Test(expected = UnitilsException.class)
    public void testAddingTwoPrimaryKeyColumnsForSameName() throws Exception {
        addPrimaryKeyColumn(expectedRow, "column", "value");
        addPrimaryKeyColumn(expectedRow, "column", "value");
    }


    private void assertColumnDifference(RowDifference result, String columnName, String expectedValue, String actualValue) {
        ColumnDifference columnDifference = result.getColumnDifference(columnName);
        assertEquals(expectedValue, columnDifference.getColumn().getValue());
        assertEquals(actualValue, columnDifference.getActualColumn().getValue());
    }


    private void assertMissingColumn(RowDifference rowDifference, String columnName) {
        for (Column missingColumn : rowDifference.getMissingColumns()) {
            if (columnName.equals(missingColumn.getName())) {
                return;
            }
        }
        fail("No missing column found for name " + columnName);
    }


    private void addPrimaryKeyColumn(Row row, String columnName, String value) {
        row.addPrimaryKeyColumn(new Column(columnName, VARCHAR, value));
    }


    private void addColumn(Row row, String columnName, String value) {
        row.addColumn(new Column(columnName, VARCHAR, value));
    }

}