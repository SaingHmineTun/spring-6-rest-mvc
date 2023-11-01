package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Primary
@Service
@RequiredArgsConstructor
public class BeerServiceJpa implements BeerService {

    public final BeerRepository beerRepository;
    public final BeerMapper beerMapper;

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
        if (beerRepository.existsById(uuid)) {
            beerRepository.deleteById(uuid);
            return true;
        }
        return false;
    }

    @Override
    public Optional<BeerDTO> updateBeerContentById(UUID uuid, BeerDTO beer) {
        AtomicReference<BeerDTO> beerDTOAtomicReference = new AtomicReference<>(null);
        getBeerById(uuid).ifPresent(foundBeer -> {
            if (beer.getBeerName() != null) {
                foundBeer.setBeerName(beer.getBeerName());
            }
            if (beer.getBeerStyle() != null) {
                foundBeer.setBeerStyle(beer.getBeerStyle());
            }
            if (beer.getPrice() != null) {
                foundBeer.setPrice(beer.getPrice());
            }
            if (beer.getQuantityOnHand() != null) {
                foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            }
            if (beer.getUpc() != null) {
                foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            }
            Beer updatedBeer = beerRepository.save(beerMapper.beerDtoToBeer(foundBeer));
            beerDTOAtomicReference.set(beerMapper.beerToBeerDto(updatedBeer));
        });
        return Optional.ofNullable(beerDTOAtomicReference.get());
    }

    @Override
    public List<BeerDTO> getBeerByQuery(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        List<BeerDTO> beerDTOList;
        if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerDTOList = beerRepository.findBeersByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle).stream().map(beerMapper::beerToBeerDto).toList();
        } else if (StringUtils.hasText(beerName)) {
            beerDTOList = beerRepository.findBeersByBeerNameIsLikeIgnoreCase("%" + beerName + "%").stream().map(beerMapper::beerToBeerDto).toList();
        } else if (beerStyle != null) {
            beerDTOList = beerRepository.findBeersByBeerStyle(beerStyle).stream().map(beerMapper::beerToBeerDto).toList();
        } else {
            beerDTOList = beerRepository.findAll().stream().map(beerMapper::beerToBeerDto).toList();
        }

        System.out.println(beerDTOList);

        if (showInventory != null && !showInventory) {
            beerDTOList.forEach(beerDTO -> beerDTO.setQuantityOnHand(null));
        }

        System.out.println(beerDTOList);

        return beerDTOList;
    }
}
