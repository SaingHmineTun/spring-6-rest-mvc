package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.Customer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeerOrderRepositoryTest {

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    Beer testBeer;
    Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().get(0);
        testBeer = beerRepository.findAll().get(0);
    }

    @Test
    @Transactional
    void test_emptyBeerOrder() {
        BeerOrder beerOrder = BeerOrder.builder()
                .customerRef("Test Beer Order")
                .customer(testCustomer)
                .build();
        // Save and Flush to see beerOrder list in a customer object in realtime.
        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
        System.out.println(savedBeerOrder);
    }
}