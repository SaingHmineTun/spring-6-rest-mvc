package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Primary
@Service
@RequiredArgsConstructor
public class BeerServiceJpa implements BeerService {

    public final BeerRepository beerRepository;
    public final BeerMapper beerMapper;

    @Override
    public List<BeerDTO> listBeers() {
        return beerRepository.findAll().stream().map(beerMapper::beerToBeerDto).toList();
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
//        return beerRepository.findById(id).map(beerMapper::beerToBeerDto);
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(id).orElse(null)));
    }

    @Override
    public BeerDTO addBeer(BeerDTO beerDTO) {
        Beer beer1 = beerRepository.save(beerMapper.beerDtoToBeer(beerDTO));
        return beerMapper.beerToBeerDto(beer1);
    }

    @Override
    public Optional<BeerDTO> updateBeer(UUID id, BeerDTO beerDTO) {
        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>(null);
        getBeerById(id).ifPresent(foundBeer -> {
            foundBeer.setBeerName(beerDTO.getBeerName());
            foundBeer.setBeerStyle(beerDTO.getBeerStyle());
            foundBeer.setUpc(beerDTO.getUpc());
            foundBeer.setPrice(beerDTO.getPrice());
            foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
            BeerDTO beerDTO1 = beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(foundBeer)));
            atomicReference.set(beerDTO1);
        });
        return Optional.ofNullable(atomicReference.get());
    }

    @Override
    public boolean deleteBeerById(UUID uuid) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        if (getBeerById(uuid).isPresent()) {
            beerRepository.deleteById(uuid);
            atomicBoolean.set(true);
        }
        return atomicBoolean.get();
    }

    @Override
    public boolean updateBeerContentById(UUID uuid, BeerDTO beer) {
        return false;
    }
}
