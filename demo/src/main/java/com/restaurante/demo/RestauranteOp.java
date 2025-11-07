package com.restaurante.demo;

import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.restaurante.demo.repositories.UsuariosRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@SpringBootApplication
@RestController
public class RestauranteOp {

    @Autowired
    private UsuariosRepository usuariosRepository;
    public static void main(String[] args) {
        SpringApplication.run(RestauranteOp.class, args);
    }
    
    //url: http://localhost:8282/addUser?nome='nome'&email='email@email.com'&senha=1234&telefone=999999999
    @GetMapping("/addUser")
    public String adicionarUsuario(@RequestParam String nome, String email, String senha, String telefone) {

        Usuarios usuario = criaUsuario(nome, email, senha, telefone);
        
        usuariosRepository.save(usuario);
        
        return usuario.toString();
    }

    /*
    Exemplo de JSON para teste via Postman ou Insomnia
    {
        "nome": "arnaldo",
        "email": "arnaldo@email.com",
        "senha": "1234",
        "telefone": "999999999"
    }
    */

    @PostMapping("/addUser")
    public String adicionarUsuario(@RequestBody Usuarios usuario) {

        usuariosRepository.save(usuario);

        return usuario.toString();
    }
    
    
    public Usuarios criaUsuario(String nome, String email, String senha, String telefone) {
        Usuarios u = new Usuarios();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(senha);
        u.setTelefone(telefone);
        return u;
    }

    @GetMapping("/usuarios")
    public List<Usuarios> listarUsuarios() {
        return usuariosRepository.findAll();
    }
    
    
}
