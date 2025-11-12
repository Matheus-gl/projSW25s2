package com.restaurante.demo;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.restaurante.demo.repositories.UsuariosRepository;

@SpringBootTest
@AutoConfigureMockMvc
class RestauranteOpTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void deveCriarUsuario() throws Exception {
	mockMvc.perform(get("/restaurante/criaUsuario")
			.param("nome", "teste")
			.param("email", "teste@email.com")
			.param("senha", "1234")
			.param("telefone", "999999999"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome").value("teste"))
			.andExpect(jsonPath("$.email").value("teste@email.com"))
			.andExpect(jsonPath("$.senha").value("1234"))
			.andExpect(jsonPath("$.telefone").value("999999999"));
	}

	@Test
    void deveListarUsuarios() throws Exception {
        mockMvc.perform(get("/restaurante/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarMesas() throws Exception {
        mockMvc.perform(get("/restaurante/mesas"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarReservas() throws Exception {
        mockMvc.perform(get("/restaurante/reservas"))
                .andExpect(status().isOk());
    }
}