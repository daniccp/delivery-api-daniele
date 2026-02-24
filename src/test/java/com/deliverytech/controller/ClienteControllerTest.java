package com.deliverytech.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_CLIENTE")
public class ClienteControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void deveCriarClienteComSucesso() throws Exception {
        String json = "{\"nome\":\"Felipe\",\"email\":\"felipe.valido@teste.com\"}";

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void naoDeveCriarClienteComNomeEmBranco() throws Exception {
        // Testa a validação @NotBlank no campo 'nome'.
        String json = "{\"nome\":\"\",\"email\":\"email.qualquer@teste.com\"}";

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarClienteComEmailEmBranco() throws Exception {
        // Tenta criar um cliente com o email vazio.
        // A API deve retornar um erro 400 (Bad Request) por causa da anotação @NotBlank.
        String json = "{\"nome\":\"Nome Valido\",\"email\":\"\"}";

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarClienteComEmailInvalido() throws Exception {
        //  Tenta criar um cliente com um email de formato inválido.
        // A API deve retornar um erro 400 (Bad Request) por causa da anotação @Email.
        String json = "{\"nome\":\"Nome Valido\",\"email\":\"email-invalido.com\"}";

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}