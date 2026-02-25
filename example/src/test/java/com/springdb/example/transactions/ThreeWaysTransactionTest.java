package com.springdb.example.transactions;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import com.springdb.example.service.transactions.DeclarativeTransactionComponent;
import com.springdb.example.service.transactions.EntityManagerTransactionComponent;
import com.springdb.example.service.transactions.ProgrammaticTemplateComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ThreeWaysTransactionTest extends AbstractIntegrationTest {

    @Autowired private DeclarativeTransactionComponent declarative;
    @Autowired private ProgrammaticTemplateComponent programmatic;
    @Autowired private EntityManagerTransactionComponent emComponent;
    @Autowired private JpaCarRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testAllTransactionMethods() {
        CarEntity carDecl = new CarEntity();
        carDecl.setBrand("Declarative");
        carDecl.setSeatingCapacity(2);

        declarative.transaction1(carDecl);
        Long declId = carDecl.getId();
        assertNotNull(declId);

        declarative.transaction2(declId, 5);
        assertEquals(5, repository.findById(declId).get().getSeatingCapacity());

        declarative.transaction3(declId);
        assertFalse(repository.existsById(declId));
        CarEntity carProg = new CarEntity();
        carProg.setBrand("Programmatic");
        carProg.setSeatingCapacity(4);

        programmatic.transaction1(carProg);
        Long progId = carProg.getId();
        assertNotNull(progId);

        programmatic.transaction2(progId, 7);
        assertEquals(7, repository.findById(progId).get().getSeatingCapacity());

        programmatic.transaction3(progId);
        assertFalse(repository.existsById(progId));

        CarEntity carEm = new CarEntity();
        carEm.setBrand("EntityManager");
        carEm.setSeatingCapacity(5);

        emComponent.transaction1(carEm);
        Long emId = carEm.getId();
        assertNotNull(emId);

        emComponent.transaction2(emId, 9);
        assertEquals(9, repository.findById(emId).get().getSeatingCapacity());

        emComponent.transaction3(emId);
        assertFalse(repository.existsById(emId));

        assertEquals(0, repository.count());
    }
}