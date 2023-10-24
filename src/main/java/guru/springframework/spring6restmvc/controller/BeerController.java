package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.service.BeerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@AllArgsConstructor
@RestController
public class BeerController {

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerService beerService;

    @GetMapping(BEER_PATH)
    public ResponseEntity<List<BeerDTO>> listBeers() {
        return ResponseEntity.ok(beerService.listBeers());
    }

    @GetMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> getBeerById(@PathVariable("beerId") UUID id) {
        BeerDTO beer = beerService.getBeerById(id).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(beer);
    }

    @PostMapping(BEER_PATH)
    public ResponseEntity<URI> createNewBear(@RequestBody BeerDTO beer) {
        BeerDTO addedBeer = beerService.addBeer(beer);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedBeer.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<URI> updateBeerById(@PathVariable("beerId") UUID id, @RequestBody BeerDTO beer) {
        boolean isUpdated = beerService.updateBeer(id, beer);
        if (isUpdated) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<Void> updateBeerContentById(@PathVariable("beerId") UUID uuid, @RequestBody BeerDTO beer) {
        if (beerService.updateBeerContentById(uuid, beer)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity<Void> deleteBeerById(@PathVariable("beerId") UUID uuid) {
        boolean isDeleted = beerService.deleteBeerById(uuid);
        if (isDeleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

}
