package org.unitils.inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.inject.InjectMultipleTargetsTest.Address;
import org.unitils.inject.InjectMultipleTargetsTest.Animal;
import org.unitils.inject.InjectMultipleTargetsTest.Person;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;


/**
 * Check if the obj is still injected in the targets using {@link org.unitils.inject.annotation.TestedObject}.
 *
 * @author Willemijn Wouters
 *
 * @since 3.4.3
 *
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class InjectMultipleTargets2Test {


    /**
     * with tested objects
     */
    @TestedObject
    private Person person;

    @TestedObject
    private Person person2;

    @TestedObject
    private Animal animal2;

    @InjectIntoByType
    private Address address2 = new Address("5", "Nieuwstraat");

    @Test
    public void test() {
        Assert.assertNotNull(person);
        Assert.assertNotNull(animal2);
        Assert.assertNotNull(person2);

        Assert.assertNotNull(person.getAddress());
        Assert.assertNotNull(person2.getAddress());
        Assert.assertNotNull(animal2.getAddress());;
    }

}
