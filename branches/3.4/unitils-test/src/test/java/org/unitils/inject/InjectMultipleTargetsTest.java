package org.unitils.inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.inject.annotation.InjectIntoByType;


/**
 * Check if the obj is correctly injected in the target without using {@link org.unitils.inject.annotation.TestedObject}.
 *
 * @author Willemijn Wouters
 *
 * @since 3.4.3
 *
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class InjectMultipleTargetsTest {

    private Person person = new Person(null);

    private Animal animal = new Animal(null);

    private Person person2 = new Person(null);

    @InjectIntoByType(target = {"person", "animal"})
    private Address address = new Address("5", "Nieuwstraat");




    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        Assert.assertNotNull(person);
        Assert.assertNotNull(animal);
        Assert.assertNotNull(person2);
        Assert.assertNotNull(animal.getAddress());
        Assert.assertNotNull(person.getAddress());
        Assert.assertNull(person2.getAddress());
    }



    protected static class Animal {

        private Address address;



        /**
         *
         */
        public Animal() {
        }
        /**
         * @param name
         * @param address
         */
        public Animal(Address address) {
            super();
            this.address = address;
        }

        /**
         * @return the address
         */
        public Address getAddress() {
            return address;
        }


    }

    protected static class Person {


        /**
         *
         */
        public Person() {
        }

        private Address address;

        /**
         * @param name
         * @param address
         */
        public Person(Address address) {
            super();
            this.address = address;
        }


        /**
         * @return the address
         */
        public Address getAddress() {
            return address;
        }
    }

    protected static class Address {

        private String number;
        private String street;
        /**
         * @param number
         * @param street
         */
        public Address(String number, String street) {
            super();
            this.number = number;
            this.street = street;
        }


        /**
         * @return the number
         */
        public String getNumber() {
            return number;
        }


        /**
         * @return the street
         */
        public String getStreet() {
            return street;
        }
    }

}
