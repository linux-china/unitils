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
package org.unitils.dbmaintainer.structure.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;

/**
 * Default implementation of {@link ConstraintsDisabler}.
 * This will disable all foreign key, check and not-null constraints on the configured database schemas.
 * Primary key constraints will not be disabled.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Bart Vermeiren
 */
public class DefaultConstraintsDisabler extends BaseDatabaseAccessor implements ConstraintsDisabler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultConstraintsDisabler.class);


    /**
     * Disable every foreign key or not-null constraint
     */
    public void disableConstraints() {
        for (DbSupport dbSupport : dbSupports) {
            logger.info("Disabling constraints in database schema " + dbSupport.getSchemaName());

            // first disable referential constraints to avoid conflicts
            disableReferentialConstraints(dbSupport);
            // disable not-null and check constraints
            disableValueConstraints(dbSupport);
        }
    }


    /**
     * Disables all referential constraints (e.g. foreign keys) on all tables in the schema
     *
     * @param dbSupport The dbSupport for the database, not null
     */
    protected void disableReferentialConstraints(DbSupport dbSupport) {
        try {
            dbSupport.disableReferentialConstraints();
        } catch (Throwable t) {
            logger.error("Unable to remove referential constraints.", t);
        }
    }


    /**
     * Disables all value constraints (e.g. not null) on all tables in the schema
     *
     * @param dbSupport The dbSupport for the database, not null
     */
    protected void disableValueConstraints(DbSupport dbSupport) {
        try {
            dbSupport.disableValueConstraints();
        } catch (Throwable t) {
            logger.error("Unable to remove value constraints.", t);
        }
    }
}