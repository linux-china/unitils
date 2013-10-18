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
package org.unitils.spring.util;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.AnnotatedInstanceManager;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * A class for managing and creating Spring application contexts.
 * <p/>
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ApplicationContextManager extends AnnotatedInstanceManager<ApplicationContext, SpringApplicationContext> {

    /**
     * Factory for creating ApplicationContexts
     */
    protected ApplicationContextFactory applicationContextFactory;


    /**
     * Creates a new instance, using the given {@link ApplicationContextFactory}. The given list of
     * <code>BeanPostProcessor</code>s will be registered on all <code>ApplicationContext</code>s that are
     * created.
     *
     * @param applicationContextFactory The factory for creating <code>ApplicationContext</code>s, not null.
     */
    public ApplicationContextManager(ApplicationContextFactory applicationContextFactory) {
        super(ApplicationContext.class, SpringApplicationContext.class);
        this.applicationContextFactory = applicationContextFactory;
    }


    /**
     * Gets the application context for the given test as described in the class javadoc. A UnitilsException will be
     * thrown if no context could be retrieved or created.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    public ApplicationContext getApplicationContext(Object testObject) {
        ApplicationContext applicationContext = getInstance(testObject);
        if (applicationContext == null) {
            throw new UnitilsException("No configuration found for creating an ApplicationContext for test " + testObject.getClass() + ". Make sure that you either specify a value " +
                    "for an @" + annotationClass.getSimpleName() + " annotation somewhere in the testclass or a superclass or that you specify a custom create method in the test class itself.");
        }
        return applicationContext;
    }


    /**
     * Checks whether the given test object has an application context linked to it. If true is returned,
     * {@link #getApplicationContext} will return an application context, If false is returned, it will raise
     * an exception.
     *
     * @param testObject The test instance, not null
     * @return True if an application context is linked
     */
    public boolean hasApplicationContext(Object testObject) {
        return hasInstance(testObject);
    }


    /**
     * Forces the reloading of the application context the next time that it is requested. If classes are given
     * only contexts that are linked to those classes will be reset. If no classes are given, all cached
     * contexts will be reset.
     *
     * @param classes The classes for which to reset the contexts
     */
    public void invalidateApplicationContext(Class<?>... classes) {
        invalidateInstance(classes);
    }


    /**
     * Creates a new application context for the given locations. The application context factory is used to create
     * the instance. After creating the context, this will also register all <code>BeanPostProcessor</code>s and
     * refresh the context.
     * <p/>
     * Note: for this to work, the application context may not have been refreshed in the factory.
     * By registering the bean post processors before the refresh, we can intercept bean creation and bean wiring.
     * This is no longer possible if the context is already refreshed.
     * @param locations The locations where to find configuration files, not null
     *
     * @return the context, not null
     */
    @Override
    protected ApplicationContext createInstanceForValues(Object testObject, Class<?> testClass, List<String> locations) {
    	try {
            // create application context
            final ConfigurableApplicationContext applicationContext = applicationContextFactory.createApplicationContext(locations);
            // load application context
            applicationContext.refresh();
            return applicationContext;

        } catch (Throwable t) {
            throw new UnitilsException("Unable to create application context for locations " + locations, t);
        }
    }
   

    /**
     * Gets the locations that are specified for the given {@link SpringApplicationContext} annotation. An array with
     * 1 empty string should be considered to be empty and null should be returned.
     *
     * @param annotation The annotation, not null
     * @return The locations, null if no values were specified
     */
    @Override
    protected List<String> getAnnotationValues(SpringApplicationContext annotation) {
        String[] locations = annotation.value();
        return asList(locations);
    }
}
