package guru.springframework.spring6restmvc.service;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeerCsvServiceTest {

    @Test
    public void convertCSV() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        var beerCsvList = new BeerCsvServiceImpl().convertCSV(file);
        assertThat(beerCsvList.size()).isGreaterThan(0);
    }

}