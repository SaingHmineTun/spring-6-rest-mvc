package guru.springframework.spring6restmvc.service;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {
    @Override
    public List<BeerCsvRecord> convertCSV(File file) {
        try {
            List<BeerCsvRecord> beerCsvRecords = new CsvToBeanBuilder<BeerCsvRecord>(new FileReader(file))
                    .withType(BeerCsvRecord.class)
                    .build().parse();
            return beerCsvRecords;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
