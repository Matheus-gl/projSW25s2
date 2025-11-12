package com.restaurante.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.restaurante.demo.repositories.MesasRepository;
import com.restaurante.demo.repositories.ReservasRepository;
import com.restaurante.demo.repositories.UsuariosRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@SpringBootApplication

@RestController
@RequestMapping("/restaurante")
public class RestauranteOp {

    @Autowired
    private UsuariosRepository usuariosRepository;
    @Autowired
    private MesasRepository mesasRepository;
    @Autowired
    private ReservasRepository reservasRepository;

    public static void main(String[] args) {
        SpringApplication.run(RestauranteOp.class, args);
    }
    
    //url: http://localhost:8282/addUser?nome='nome'&email='email@email.com'&senha=1234&telefone=999999999
    public String adicionarUsuario(
        @RequestParam String nome,
        @RequestParam String email,
        @RequestParam String senha,
        @RequestParam String telefone) {

        Usuarios usuario = criaUsuario(nome, email, senha, telefone);
        usuariosRepository.save(usuario);
        return "Usuário adicionado com sucesso: " + usuario.getNome();
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
    public Usuarios adicionarUsuario(@RequestBody Usuarios usuario) {
        return usuariosRepository.save(usuario);
    }

    @PostMapping("/addMesa")
    public Mesas adicionarMesa(@RequestBody Mesas mesa) {
        return mesasRepository.save(mesa);
    }

    @PostMapping("/addReserva")
    public Reservas adicionarReserva(@RequestBody Reservas reserva) {
        return reservasRepository.save(reserva);
    }
    
    @GetMapping("/criaUsuario")
    public Usuarios criaUsuario(String nome, String email, String senha, String telefone) {
        Usuarios u = new Usuarios();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(senha);
        u.setTelefone(telefone);
        return u;
    }

    @GetMapping("/criaMesa")
    public Mesas criaMesa(int numero_mesa, int capacidade, StatusMesaEnum status) {
        Mesas m = new Mesas();
        m.setNumero_mesa(numero_mesa);
        m.setCapacidade(capacidade);
        m.setStatus(status);
        return m;
    }

    @GetMapping("/criaReserva")
    public Reservas criaReserva(Integer idMesa, Integer idUsuario, LocalDate dataReserva,
                    LocalTime horarioInicio, LocalTime horarioFim) {
        Reservas r = new Reservas();
        r.setIdMesa(idMesa);
        r.setIdUsuario(idUsuario);
        r.setDataReserva(dataReserva);
        r.setHorarios(horarioInicio, horarioFim);
        return r;
    }

    @GetMapping("/usuarios")
    public List<Usuarios> listarUsuarios() {
        return usuariosRepository.findAll();
    }

    @GetMapping("/mesas")
    public List<Mesas> listarMesas() {
        return mesasRepository.findAll();
    }

    @GetMapping("/reservas")
    public List<Reservas> listarReservas() {
        return reservasRepository.findAll();
    }
    
    
}
