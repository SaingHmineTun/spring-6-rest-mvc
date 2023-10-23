package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.Beer;

import java.util.List;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
public interface BeerService {

    List<Beer> listBeers();

    Beer getBeerById(UUID id);

    Beer addBeer(Beer beer);

    boolean updateBeer(UUID id, Beer beer);

    boolean deleteBeerById(UUID uuid);

    boolean updateBeerContentById(UUID uuid, Beer beer);
}
