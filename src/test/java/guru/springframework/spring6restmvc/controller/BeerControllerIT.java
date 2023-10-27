package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    BeerRepository beerRepository;

    // We call BeerController directly in Integration Test
    @Autowired
    BeerController beerController;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BeerMapper beerMapper;

    @Test
    void test_getAllBeer() {

        List<BeerDTO> beerDTOList = beerController.listBeers().getBody();

        assertThat(beerDTOList.size()).isEqualTo(3);

    }

    @Test
    @Rollback
    @Transactional // To go back to the original state
    void test_getAllBeer_EmptyList() {
        beerRepository.deleteAll();
        List<BeerDTO> beerDTOList = beerController.listBeers().getBody();
        assertThat(beerDTOList.size()).isEqualTo(0);
    }

    @Test
    void test_getBeerById() {
        BeerDTO beer = beerController.listBeers().getBody().get(0);
        BeerDTO beerDTO = beerController.getBeerById(beer.getId()).getBody();
        /*
        Confusion is that when u sent variable to controller, it always throws 404!
         */
//        UUID id = UUID.fromString("badf0f81-46aa-486d-9b78-b5821a0dc6d2");
//        BeerDTO beerDTO = beerController.getBeerById(id).getBody();
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getBeerName()).isEqualTo("Galaxy Cat");
    }

    @Test
    void test_getBeerById_Exception() {
        assertThrows(NotFoundException.class, () -> {
            beerController.getBeerById(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void test_saveBeer() {
        Map<String, Object> beerMap = Map.of(
                "beerName", "Khufra",
                "beerStyle", "GOSE",
                "upc", "287353",
                "quantityOnHand", 50,
                "price", 7.99
        );
        BeerDTO beerDTO = objectMapper.convertValue(beerMap, BeerDTO.class);
        ResponseEntity<URI> responseEntity = beerController.createNewBear(beerDTO);
        System.out.println(responseEntity.getHeaders().getLocation().toString());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String savedBeerId = responseEntity.getHeaders().getLocation().toString().substring(
                responseEntity.getHeaders().getLocation().toString().lastIndexOf("/") + 1
        );
        System.out.println(savedBeerId);
        BeerDTO beerDTO1 = beerController.getBeerById(UUID.fromString(savedBeerId)).getBody();
        assertThat(beerDTO1).isNotNull();
    }

    @Rollback
    @Transactional
    @Test
    void test_updateBeer() {

        Beer beer = beerRepository.findAll().get(0);

        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        beerDTO.setCreatedDate(null);
        beerDTO.setUpdateDate(null);
        final String beerName = beerDTO.getBeerName() + " UPDATED";
        beerDTO.setBeerName(beerName);

        var resEnt = beerController.updateBeerById(beer.getId(), beerDTO);
        assertThat(resEnt.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resEnt.getBody().getBeerName()).isEqualTo(beerName);

    }

    @Rollback
    @Transactional
    @Test
    void test_updateBeer_NotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.updateBeerById(UUID.randomUUID(), BeerDTO.builder().build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void test_deleteById() {
        Beer beer = beerRepository.findAll().get(0);
        var resEnt = beerController.deleteBeerById(beer.getId());
        assertThat(resEnt.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThrows(NotFoundException.class, () -> {
            beerController.getBeerById(beer.getId());
        });
    }

    @Test
    void test_deleteById_NotFound() {
        assertThrows(NotFoundException.class, () -> {
           beerController.deleteBeerById(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void test_updatePatchById() {
        BeerDTO toUpdate = beerController.listBeers().getBody().get(1);
        final String beerName = toUpdate.getBeerName() + " UPDATED";
        BeerDTO beerDTO = objectMapper.convertValue(Map.of("beerName", beerName), BeerDTO.class);
        ResponseEntity<BeerDTO> resEnt = beerController.updateBeerContentById(toUpdate.getId(), beerDTO);

        assertThat(resEnt.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resEnt.getBody().getBeerName()).isEqualTo(beerName);
    }

    @Test
    void test_updatePath_NotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.updateBeerContentById(UUID.randomUUID(), BeerDTO.builder().build());
        });
    }

    /**
     * Controller Testing with JPA
     * This is Full Spring Boot Test
     */

    // We have to use WebApplicationContext to send Http Request
    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void test_saveBeerBadName() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        mockMvc.perform(patch(BEER_PATH_ID, beer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BeerDTO.builder()
                        .beerName("KhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufraKhufra")
                        .build())
                )
        ).andExpect(status().isBadRequest());
    }
}


