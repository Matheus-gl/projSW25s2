package com.restaurante.demo;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.restaurante.demo.repositories.UsuariosRepository;

@SpringBootTest
@AutoConfigureMockMvc
class RestauranteOpTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
    private RestauranteOp restauranteOp;

	@MockBean
	private UsuariosRepository usuariosRepository;

	@Test
    void deveAdicionarUsuarioSemSalvarNoBanco() throws Exception {
        // JSON de entrada
        String jsonUsuario = """
            {
                "nome": "Usuário Mock",
                "email": "mock@email.com",
                "senha": "1234",
                "telefone": "999999999"
            }
            """;

        // Usa o método criaUsuario() para gerar o usuário simulado
        Usuarios mockUser = restauranteOp.criaUsuario(
                "Usuário Mock",
                "mock@email.com",
                "1234",
                "999999999"
        );
        mockUser.setIdUsuario(1); // só pra simular um ID gerado pelo banco

        // Configura o comportamento do repositório mockado
        when(usuariosRepository.save(org.mockito.ArgumentMatchers.any(Usuarios.class)))
                .thenReturn(mockUser);

        // Executa o endpoint
        mockMvc.perform(post("/restaurante/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Usuário Mock"))
                .andExpect(jsonPath("$.email").value("mock@email.com"));
    
    	}

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