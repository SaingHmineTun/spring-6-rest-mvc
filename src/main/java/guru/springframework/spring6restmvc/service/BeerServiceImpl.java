package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private Map<UUID, Beer> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();

        Beer beer1 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Beer beer2 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Beer beer3 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        beerMap.put(beer1.getId(), beer1);
        beerMap.put(beer2.getId(), beer2);
        beerMap.put(beer3.getId(), beer3);
    }

    @Override
    public List<Beer> listBeers(){
        return new ArrayList<>(beerMap.values());
    }

    @Override
    public Beer getBeerById(UUID id) {

        log.debug("Get Beer by Id - in service. Id: " + id.toString());

        return beerMap.get(id);
    }

    @Override
    public Beer addBeer(Beer beer) {

        /*
        If using with database, id, createdDate, updatedDate must be only updated in it
         */
        Beer savedBeer = Beer.builder()
                .id(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .beerName(beer.getBeerName())
                .beerStyle(beer.getBeerStyle())
                .price(beer.getPrice())
                .quantityOnHand(beer.getQuantityOnHand())
                .upc(beer.getUpc())
                .build();
        beerMap.put(savedBeer.getId(), savedBeer);
        return savedBeer;
    }

    @Override
    public boolean updateBeer(UUID id, Beer beer) {
        Beer updatedBeer = beerMap.get(id);
        if (updatedBeer != null) {
            updatedBeer.setBeerName(beer.getBeerName());
            updatedBeer.setBeerStyle(beer.getBeerStyle());
            updatedBeer.setPrice(beer.getPrice());
            updatedBeer.setUpc(beer.getUpc());
            updatedBeer.setQuantityOnHand(beer.getQuantityOnHand());
            updatedBeer.setUpdateDate(LocalDateTime.now());
            beerMap.put(id, updatedBeer);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteBeerById(UUID uuid) {
        Beer toDeleteBeer = getBeerById(uuid);
        if (toDeleteBeer != null) {
            beerMap.remove(uuid);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateBeerContentById(UUID uuid, Beer beer) {
        Beer existingBeer = beerMap.get(uuid);
        if (existingBeer != null) {
            if (beer.getBeerName() != null) {
                existingBeer.setBeerName(beer.getBeerName());
            }
            if (beer.getBeerStyle() != null) {
                existingBeer.setBeerStyle(beer.getBeerStyle());
            }
            if (beer.getPrice() != null) {
                existingBeer.setPrice(beer.getPrice());
            }
            if (beer.getQuantityOnHand() != null) {
                existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
            }
            if (beer.getUpc() != null) {
                existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
            }
            beerMap.put(uuid, existingBeer);
        }
        return false;
    }
}
