package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Primary
@Service
@RequiredArgsConstructor
public class BeerServiceJpa implements BeerService {

    public final BeerRepository beerRepository;
    public final BeerMapper beerMapper;

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

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
    public Page<BeerDTO> getBeerByQuery(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        Page<BeerDTO> beerDTOPage;

        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerDTOPage = beerRepository.findBeersByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, pageRequest).map(beerMapper::beerToBeerDto);
        } else if (StringUtils.hasText(beerName)) {
            beerDTOPage = beerRepository.findBeersByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageRequest).map(beerMapper::beerToBeerDto);
        } else if (beerStyle != null) {
            beerDTOPage = beerRepository.findBeersByBeerStyle(beerStyle, pageRequest).map(beerMapper::beerToBeerDto);
        } else {
            beerDTOPage = new PageImpl<>(beerRepository.findAll().stream().map(beerMapper::beerToBeerDto).toList());
        }

        System.out.println(beerDTOPage);

        if (showInventory != null && !showInventory) {
            beerDTOPage.forEach(beerDTO -> beerDTO.setQuantityOnHand(null));
        }

        System.out.println(beerDTOPage);



        return beerDTOPage;
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber, queryPageSize;
        if (pageNumber != null && pageNumber > 0) {
            // Page 1 = Page Index 0
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize != null) {
            if (pageSize > 1000)
                queryPageSize = 1000;
            else
                queryPageSize = pageSize;
        } else {
            queryPageSize = DEFAULT_PAGE_SIZE;
        }

        return PageRequest.of(queryPageNumber, queryPageSize);

    }
}
