package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
public interface BeerService {

    Optional<BeerDTO> getBeerById(UUID id);

    BeerDTO addBeer(BeerDTO beer);

    Optional<BeerDTO> updateBeer(UUID id, BeerDTO beer);

    boolean deleteBeerById(UUID uuid);

    Optional<BeerDTO> updateBeerContentById(UUID uuid, BeerDTO beer);


    Page<BeerDTO> getBeerByQuery(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize);
}
