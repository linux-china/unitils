package org.unitils.dbmaintainer.script.impl;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.dbmaintainer.script.Script;


/**
 * ExtendedScriptSourceLoadAllScriptsTest.
 * 
 * @author wiw
 * 
 * @since 3.4
 * 
 */
public class ExtendedScriptSourceLoadAllScriptsTest {
    
    private ExtendedScriptSource sut;
    
    private String scriptLocation = "org/unitils/dbunit/testdbscripts/";

    private static final String PROP_IGNORE = "dbMaintainer.script.locations.ignore";
    
    private Properties config;
    
    @Before
    public void setUp() {
        config = new Properties();
        config.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_LOCATIONS, "org/unitils/dbunit/testdbscripts/");
        config.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_EXTENSIONS, "sql");
        config.setProperty(DefaultScriptSource.PROPKEY_POSTPROCESSINGSCRIPT_DIRNAME, "postprocessing");
        config.setProperty(DefaultScriptSource.PROPKEY_USESCRIPTFILELASTMODIFICATIONDATES, "false");
        
    }
    @Test
    public void testIgnoreListDoesNotStartWithSlash() {
        
        config.setProperty(PROP_IGNORE, scriptLocation + "testsubpackage");
        sut = new ExtendedScriptSource();
        sut.init(config);
        
        
        List<Script> actual = sut.loadAllScripts();
        Assert.assertEquals(3, actual.size());
    }

}
