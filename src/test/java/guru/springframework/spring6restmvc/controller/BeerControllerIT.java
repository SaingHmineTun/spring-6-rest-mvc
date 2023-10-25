package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    BeerRepository beerRepository;

    // We call BeerController directly in Integration Test
    @Autowired
    BeerController beerController;

    @Test
    void test_getAllBeer() {

        List<BeerDTO> beerDTOList = beerController.listBeers().getBody();

        assertThat(beerDTOList.size()).isEqualTo(3);

    }

    @Test
    @Rollback
    @Transactional // To go back to the original state
    void test_getAllBeer_EmptyList() {
        beerRepository.deleteAll();
        List<BeerDTO> beerDTOList = beerController.listBeers().getBody();
        assertThat(beerDTOList.size()).isEqualTo(0);
    }

    @Test
    void test_getBeerById() {
        BeerDTO beer = beerController.listBeers().getBody().get(0);
        BeerDTO beerDTO = beerController.getBeerById(beer.getId()).getBody();
        /*
        Confusion is that when u sent variable to controller, it always throws 404!
         */
//        UUID beerId = UUID.fromString("badf0f81-46aa-486d-9b78-b5821a0dc6d2");
//        BeerDTO beerDTO = beerController.getBeerById(beerId).getBody();
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getBeerName()).isEqualTo("Galaxy Cat");
    }

    @Test
    void test_getBeerById_Exception() {
        assertThrows(NotFoundException.class, () -> {
            beerController.getBeerById(UUID.randomUUID());
        });
    }
}