package guru.springframework.spring6restmvc.service;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class BeerCsvServiceTest {
    @Test
    public void convertCSV() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        var beerCsvList = new BeerCsvServiceImpl().convertCSV(file);
        assertThat(beerCsvList.size()).isGreaterThan(0);
    }
}