package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.service.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH;
import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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


        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.of(Beer.builder().id(UUID.randomUUID()).beerName("Change").build()));

        mockMvc.perform(get(BEER_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['beerName']", is("Change")));
    }

    @Test
    void test_getBeerById_ThrowNotFoundException() throws Exception {

        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_getAllBeers() throws Exception {

        given(beerService.listBeers()).willReturn(List.of(beer));

        mockMvc.perform(get(BEER_PATH)
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

        mockMvc.perform(post(BEER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"));

    }

    @Test
    void test_updateBeer() throws Exception {
        given(beerService.updateBeer(any(UUID.class), any(Beer.class))).willReturn(true);

        mockMvc.perform(put(BEER_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

    }

    @Test
    void test_updateBeer_notFound() throws Exception {
        given(beerService.updateBeer(any(UUID.class), any(Beer.class))).willReturn(false);

        mockMvc.perform(put(BEER_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNotFound());

    }

    @Test
    void test_deleteBeer() throws Exception {

        UUID arg = UUID.randomUUID();

        given(beerService.deleteBeerById(any(UUID.class))).willReturn(true);

        mockMvc.perform(delete(BEER_PATH_ID, arg)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(beerService).deleteBeerById(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(arg);

    }

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;
    @Captor
    ArgumentCaptor<Beer> beerArgumentCaptor;

    @Test
    void test_updatePatchBeer() throws Exception {
        Map<String, String> beerMap = Map.of("beerName", "Lolita");
        String beerJson = objectMapper.writeValueAsString(beerMap);

        UUID uuid = UUID.randomUUID();

        given(beerService.updateBeerContentById(any(UUID.class), any(Beer.class))).willReturn(true);

        mockMvc.perform(patch(BEER_PATH_ID, uuid)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerJson))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerContentById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(uuid);
        assertThat(beerArgumentCaptor.getValue().getBeerName()).isEqualTo(beerMap.get("beerName"));

    }


}