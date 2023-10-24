package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
class BeerRepositoryTest {
    @Autowired
    BeerRepository beerRepository;

    @Test
    void test_saveBeer() {
        Beer beer = beerRepository.save(Beer.builder().beerName("Lollita").build());
        assertThat(beer).isNotNull();
        assertThat(beer.getBeerName()).isEqualTo("Lollita");
        assertThat(beer.getId()).isNotNull();
    }
}