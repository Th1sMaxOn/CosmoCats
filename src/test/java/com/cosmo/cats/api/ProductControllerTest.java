package com.cosmo.cats.api;

import com.cosmo.cats.application.ProductService;
import com.cosmo.cats.dto.ProductDTO;
import com.cosmo.cats.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.cosmo.cats.integration.PriceRateClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockBean
    private PriceRateClient priceRateClient;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    ProductService service;

    @MockBean
    ProductMapper mapper;

    private ProductDTO validDto() {
        ProductDTO d = new ProductDTO();
        d.setName("Galaxy Sword");
        d.setDescription("desc");
        d.setPrice(new BigDecimal("1.00"));
        d.setCurrency("USD");
        d.setCategoryId(UUID.randomUUID().toString());
        return d;
    }
    
    @Test
    void createInvalidProduct_ShouldReturnBadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createValidAndInvalid() throws Exception {
        ProductDTO dto = validDto();
        // valid -> expect 201 (created)
        given(mapper.toEntity(any())).willAnswer(i -> {
            ProductDTO in = (ProductDTO) i.getArguments()[0];
            com.cosmo.cats.domain.model.Product p = new com.cosmo.cats.domain.model.Product();
            p.setName(in.getName()); p.setDescription(in.getDescription()); p.setPrice(in.getPrice());
            p.setCurrency(in.getCurrency());
            return p;
        });
        given(service.create(any())).willAnswer(i -> {
            com.cosmo.cats.domain.model.Product p = (com.cosmo.cats.domain.model.Product) i.getArguments()[0];
            p.setId(UUID.randomUUID());
            return p;
        });
        given(mapper.toDto(any())).willAnswer(i -> {
            com.cosmo.cats.domain.model.Product p = (com.cosmo.cats.domain.model.Product) i.getArguments()[0];
            ProductDTO out = new ProductDTO();
            out.setId(p.getId()); out.setName(p.getName()); out.setPrice(p.getPrice());
            out.setCurrency(p.getCurrency()); out.setDescription(p.getDescription());
            return out;
        });

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Galaxy Sword"));

        // invalid -> name too short
        ProductDTO bad = validDto();
        bad.setName("ab");
        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }
}