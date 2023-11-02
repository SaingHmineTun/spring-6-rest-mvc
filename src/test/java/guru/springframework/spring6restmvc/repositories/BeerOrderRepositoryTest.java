package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
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
        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        System.out.println(savedBeerOrder);


        BeerOrder beerOrder2 = BeerOrder.builder()
                .customerRef("Test Beer Order 2")
                .customer(testCustomer)
                .build();
        savedBeerOrder = beerOrderRepository.save(beerOrder2);
        System.out.println(savedBeerOrder);
    }

    @Test
    @Transactional
    /*
    When you add beerOrderShipment into beerOrder and you save it into database.
    beerOrderShipment is not added into database.
    So we must use Cascade.PERSIST to save both entities.
    This saves us a lot of time, because we don't need to save them separately and bind them manually again
     */
    void test_beerOrderSave_beerOrderShipment_alsoSaved() {
        BeerOrder savedBeerOrder = beerOrderRepository.save(
                BeerOrder.builder()
                        .customerRef("Test 2")
                        .customer(testCustomer)
                        .beerOrderShipment(BeerOrderShipment.builder().trackingNumber("12345").build())
                        .build()
        );
        System.out.println(savedBeerOrder);
    }
}