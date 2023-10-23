package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.service.BeerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    // Spring will configure the best options for you!
    @Autowired
    ObjectMapper objectMapper;
    Beer beer;

    @Test
    void test_getBeerById() throws Exception {


        given(beerService.getBeerById(any(UUID.class))).willReturn(Beer.builder().id(UUID.randomUUID()).beerName("Change").build());

        mockMvc.perform(get("/api/v1/beer/{beerId}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['beerName']", is("Change")));
    }

    @Test
    void test_getAllBeers() throws Exception {

        given(beerService.listBeers()).willReturn(List.of(beer));

        mockMvc.perform(get("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @BeforeEach
    void initAll() {
        beer = Beer.builder()
                .id(UUID.randomUUID())
                .beerName("Dagon")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("7.99"))
                .quantityOnHand(50)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
    }

    @Test
    void test_createBeer() throws Exception {

        // When sent request, don't need to have id, version, created and updated
        // These will be automatically assigned by the controller
        Beer beerToSend = beer;
        beerToSend.setId(null);
        beerToSend.setVersion(null);
        beerToSend.setCreatedDate(null);
        beerToSend.setUpdateDate(null);
        String beerJson = objectMapper.writeValueAsString(beerToSend);

        given(beerService.addBeer(any(Beer.class))).willReturn(beer);

        mockMvc.perform(post("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"));

    }

}