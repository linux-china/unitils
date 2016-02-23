package org.unitils.inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.util.Restore;


/**
 * Check if the restore still works if there are multiple targets .
 *
 * @author Willemijn Wouters
 *
 * @since 3.4.3
 *
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class InjectIntoMultipleTargetsIntoStaticTest {

    @InjectIntoStaticByType(restore = Restore.OLD_VALUE, target = {Animal.class, Person.class})
    private String name = "Suzan";

    @Test
    public void test() {
        Assert.assertEquals("Suzan", Person.getName());
        Assert.assertEquals("Suzan", Animal.getName());

    }

    @After
    public void testTearDown() {
        //check if the address is correctly restored.
        Assert.assertEquals("Myrthe", Person.getName());
        Assert.assertEquals("Maurits", Animal.getName());
    }


    private static class Animal {
        private static String name;

        static {
            name = "Maurits";
        }


        /**
         * @return the name
         */
        public static String getName() {
            return name;
        }

    }

    private static class Person {
        private static String name;


        static {
            name = "Myrthe";
        }


        /**
         * @return the name
         */
        public static String getName() {
            return name;
        }
    }

}
