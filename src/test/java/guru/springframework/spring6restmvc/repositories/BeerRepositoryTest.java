package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.service.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {
    @Autowired
    BeerRepository beerRepository;

    @Test
    void test_saveBeer() {
        Beer beer = beerRepository.save(Beer.builder()
                .beerName("Lollita")
                .beerStyle(BeerStyle.PILSNER)
                .upc("2938134")
                .price(new BigDecimal("11.99"))
                .build());
        // Without this, constraints from entities will not work
        beerRepository.flush();
        assertThat(beer).isNotNull();
        assertThat(beer.getBeerName()).isEqualTo("Lollita");
        assertThat(beer.getId()).isNotNull();
    }

    @Test
    void test_saveBeer_BeerNameTooLong() {

        assertThrows(ConstraintViolationException.class, () -> {
            beerRepository.save(Beer.builder()
                    .beerName("LollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollitaLollita")
                    .beerStyle(BeerStyle.PILSNER)
                    .upc("2938134")
                    .price(new BigDecimal("11.99"))
                    .build());
            // Without this, constraints from entities will not work
            beerRepository.flush();
        });
    }

}