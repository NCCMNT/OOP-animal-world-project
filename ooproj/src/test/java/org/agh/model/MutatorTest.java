package org.agh.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutatorTest {

    @Test
    void mutate() {
        Animal testSubject = new Animal(null, 10, null, 10, 1);
        Mutator mutator1 = new Mutator(0, 0, 10);
        System.out.println("No mutations:");
        for (int i = 0; i < 4; i++) {
            System.out.println(testSubject.getGenom());
            mutator1.mutate(testSubject);
        }
        Mutator mutator2 = new Mutator(1, 1, 10);
        System.out.println("Exactly 1 mutation:");
        for (int i = 0; i < 4; i++) {
            System.out.println(testSubject.getGenom());
            mutator2.mutate(testSubject);
        }
        Mutator mutator3 = new Mutator(10, 10, 10);
        System.out.println("Exactly 10 mutations:");
        for (int i = 0; i < 4; i++) {
            System.out.println(testSubject.getGenom());
            mutator3.mutate(testSubject);
        }
        Mutator mutator4 = new Mutator(0, 10, 10);
        System.out.println("from 0 to 10 mutations:");
        for (int i = 0; i < 4; i++) {
            System.out.println(testSubject.getGenom());
            mutator4.mutate(testSubject);
        }
    }
}