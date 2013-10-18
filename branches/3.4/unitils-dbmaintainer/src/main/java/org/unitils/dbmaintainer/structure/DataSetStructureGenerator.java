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
package org.unitils.dbmaintainer.structure;

import org.unitils.dbmaintainer.util.DatabaseAccessing;

/**
 * Generator for structure files, such as dtd or xml schema, for a DbUnit flat-xml data set file.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DataSetStructureGenerator extends DatabaseAccessing {


    /**
     * Generates the data set structure files, eg DTD or XSD
     */
    void generateDataSetStructure();

}
