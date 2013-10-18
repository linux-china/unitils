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
package org.unitils.database.transaction;

import java.util.Set;

import javax.sql.DataSource;

import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;


/**
 * Defines the contract for implementations that enable unit tests managed by unitils to be executed in a transaction.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface UnitilsTransactionManager {


    /**
     * Initialize the transaction manager
     *
     * @param transactionManagementConfigurations
     *                              Set of possible providers of a spring <code>PlatformTransactionManager</code>, not null
     */
    void init(Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations);


    /**
     * Wraps the given <code>DataSource</code> in a transactional proxy.
     * <p/>
     * The <code>DataSource</code> returned will make sure that, for the duration of a transaction, the same <code>java.sql.Connection</code>
     * is returned, and that invocations on the close() method of these connections are suppressed.
     *
     * @param dataSource The data source to wrap, not null
     * @return A transactional data source, not null
     */
    DataSource getTransactionalDataSource(DataSource dataSource);

    
    /**
     * Starts a transaction.
     *
     * @param testObject The test instance, not null
     */
    void startTransaction(Object testObject);


    /**
     * Commits the currently active transaction. This transaction must have been initiated by calling
     * {@link #startTransaction(Object)} with the same testObject within the same thread.
     *
     * @param testObject The test instance, not null
     */
    void commit(Object testObject);


    /**
     * Rolls back the currently active transaction. This transaction must have been initiated by calling
     * {@link #startTransaction(Object)} with the same testObject within the same thread.
     *
     * @param testObject The test instance, not null
     */
    void rollback(Object testObject);


    void activateTransactionIfNeeded(Object testObject);


}

