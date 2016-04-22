package org.unitils.dbunit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.ConfigUtils;
import org.unitils.dbmaintainer.locator.ClassPathDataLocator;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie;
import org.unitils.dbunit.datasetfactory.DataSetResolver;
import org.unitils.util.PropertyUtils;



/**
 * Utils class to generate the correct names for the datasets.
 *
 * @author Willemijn Wouters
 *
 * @since 3.4.4
 *
 */
public class DataSetFileNamesHandler  {

    /**
     * Gets the name of the default testdata file at class level The default name is constructed as follows: 'classname without
     * packagename'.xml
     *
     * @param testClass The test class, not null
     * @param extension The configured extension of dataset files
     * @return The default filename, not null
     */
    public String getDefaultDataSetFileNameClassLevel(Class<?> testClass, String extension) {
        String className = testClass.getName();
        StringBuilder builder = new StringBuilder();

        if (className.contains(".")) {
            className = className.replace(".", "/");

            //builder.append(className.substring(className.lastIndexOf(".") + 1));
        }
        builder.append(className);
        builder.append(".");
        builder.append(extension);
        return builder.toString();
    }


    /**
     * Gets the name of the default testdata file at class level The default name is constructed as follows: 'classname without
     * packagename'-"testmethod".xml
     *
     * @param testClass
     * @param method
     * @param extension
     * @return {@link String}
     */
    public String getDefaultDataSetFileNameMethodLevel(Class<?> testClass, Method method, String extension) {
        String className = testClass.getName();
        StringBuilder builder = new StringBuilder();
        if (className.contains(".")) {
            //className = className.replace(".", "/");
            className = className.substring(className.lastIndexOf(".") + 1);
        }
        builder.append(className);
        builder.append("-");
        builder.append(method.getName());
        builder.append(".");
        builder.append(extension);
        return builder.toString();
    }

    /**
     * Gets the name of the expected dataset file. The default name of this file is constructed as follows: 'classname without
     * packagename'.'testname'-result.xml.
     *
     * @param method The test method, not null
     * @param testClass The test class, not null
     * @param extension The configured extension of dataset files, not null
     * @return The expected dataset filename, not null
     */
    public String getDefaultExpectedDataSetFileName(Method method, Class<?> testClass, String extension) {
        String className = testClass.getName();
        return className.replace(".", "/") + "." + method.getName() + "-result." + extension;
    }

    /**
     *
     * @param nameResource
     * @param packageTestClass
     * @return {@link String}
     */
    public String generateResourceName(String nameResource, Package packageTestClass) {
        //check if the packagename is in the nameResource
        String cloneResource = new String(nameResource);

        String packageName = (packageTestClass != null) ? packageTestClass.getName() : "";
        String tempName = "";
        if (cloneResource.startsWith(packageName.replace(".", "/"))) {
            cloneResource = tempName = cloneResource.substring(packageName.length()) ;
        } else if (cloneResource.startsWith(packageName)) {
            cloneResource = tempName = cloneResource.substring(packageName.length() + 1) ;
        } else {
            tempName = cloneResource;
        }

        return cloneResource;
    }

    public String getDefaultDatasetBasedOnFilename(Class<?> testClass, Method method, String extension) {
        String name = getDefaultDataSetFileNameMethodLevel(testClass, method, extension);
        DataSetResolver dataSetResolver = getDataSetResolver();
        try {
            dataSetResolver.resolve(testClass, name);
            return testClass.getPackage().getName() + "." + name;
        } catch (Exception e) {
            // the DefaultDataSetFileNameMethodLevel does not exist.
            // so the dataset should exist on classlevel.
        }

        return getDefaultDataSetFileNameClassLevel(testClass, extension);
    }


    /**
     *
     * @param locator
     * @param nameResource
     * @param strategy
     * @param testClass
     * @return
     */
    public File locateResource(ClassPathDataLocator locator, String nameResource, ResourcePickingStrategie strategy, Class<?> testClass) {
        InputStream in = null;

        if (nameResource.startsWith("/")) {
            in = locator.getDataResource(nameResource.substring(1), strategy);
        } else {
            in = locator.getDataResource(nameResource, strategy);
        }

        if (in == null) {
            File resolvedFile = getDataSetResolver().resolve(testClass, generateResourceName(nameResource, testClass.getPackage()));
            if (resolvedFile == null) {
                throw new UnitilsException((new StringBuilder()).append("DataSetResource file with name '").append(nameResource).append("' cannot be found").toString());
            }
            try {
                in = new FileInputStream(resolvedFile);
            } catch (FileNotFoundException e) {
                throw new UnitilsException((new StringBuilder()).append("DataSetResource file with name '").append(nameResource).append("' cannot be found").toString());
            }
        }

        FileHandler fileHandler = getFileHandler();
        File tempFile = fileHandler.createTempFile(nameResource);
        fileHandler.writeToFile(tempFile, in);
        return tempFile;
    }

    /**
     * @return The data set resolver, as configured in the Unitils configuration
     */
    protected DataSetResolver getDataSetResolver() {
        return ConfigUtils.getConfiguredInstanceOf(DataSetResolver.class, getUnitilsConfiguration());
    }

    protected Properties getUnitilsConfiguration() {
        return Unitils.getInstance().getConfiguration();
    }

    protected FileHandler getFileHandler() {
        return PropertyUtils.getInstance("org.unitils.dbunit.util.FileHandler.implClassName", new FileHandler(), getUnitilsConfiguration());
    }

}
