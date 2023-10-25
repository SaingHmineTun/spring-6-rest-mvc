package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
}