package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public BeerDTO addBeer(BeerDTO beer) {
        return null;
    }

    @Override
    public boolean updateBeer(UUID id, BeerDTO beer) {
        return false;
    }

    @Override
    public boolean deleteBeerById(UUID uuid) {
        return false;
    }

    @Override
    public boolean updateBeerContentById(UUID uuid, BeerDTO beer) {
        return false;
    }
}
