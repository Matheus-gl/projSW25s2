package com.restaurante.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.restaurante.demo.repositories.MesasRepository;
import com.restaurante.demo.repositories.ReservasRepository;
import com.restaurante.demo.repositories.UsuariosRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

//@SpringBootApplication
public class RestauranteApp {

    public static void main(String[] args) {
    SpringApplication app = new SpringApplication(RestauranteApp.class);
    app.setHeadless(false); // permite AWT/Swing
    app.run(args);
	}

    @Bean
    public CommandLineRunner swingUiStarter(MesasRepository mesasRepo,
                                            UsuariosRepository usuariosRepo,
                                            ReservasRepository reservasRepo,
                                            EntityManager em) {
        return args -> {
            // Inicializa a UI no EDT
            SwingUtilities.invokeLater(() -> showLogin(mesasRepo, usuariosRepo, reservasRepo));
        };
    }

    // UI simples baseada em JOptionPane para demonstrar fluxo
    private void showLogin(MesasRepository mesasRepo,
                       UsuariosRepository usuariosRepo,
                       ReservasRepository reservasRepo) {

    boolean running = true;
    while (running) {
        String[] options = {"Login Admin", "Login Usuário / Criar", "Sair"};
        int choice = JOptionPane.showOptionDialog(null, "Escolha uma opção", "Restaurante",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            if (adminLogin()) {
                showAdminPanel(mesasRepo, usuariosRepo, reservasRepo);
            } else {
                JOptionPane.showMessageDialog(null, "Credenciais inválidas");
            }
        } else if (choice == 1) {
            Integer userId = userAuthOrCreate(usuariosRepo);
            if (userId != null) {
                showUserPanel(userId, mesasRepo, reservasRepo, usuariosRepo);
            }
        } else { // Sair / fechar
            running = false;
        }
    }
}

    private boolean adminLogin() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(4, 1, 5, 5));
        panel.add(new javax.swing.JLabel("Admin Login:"));
        javax.swing.JTextField userField = new javax.swing.JTextField(20);
        panel.add(userField);
        panel.add(new javax.swing.JLabel("Admin Senha:"));
        javax.swing.JPasswordField passField = new javax.swing.JPasswordField(20);
        panel.add(passField);

        int res = JOptionPane.showConfirmDialog(null, panel, "Login Admin",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) {
            return false;
        }

        String user = userField.getText() == null ? "" : userField.getText().trim();
        String pass = new String(passField.getPassword());
        return "admin".equals(user) && "1234".equals(pass);
    }

	private Integer userAuthOrCreate(UsuariosRepository usuariosRepo) {
		String[] opts = {"Entrar", "Criar usuário", "Voltar"};
		int c = JOptionPane.showOptionDialog(null, "Entrar ou Criar", "Usuário",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

		if (c == 0) {
			// login por email e senha em painel (email acima, senha abaixo)
			while (true) {
				javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(4, 1, 5, 5));
				panel.add(new javax.swing.JLabel("Email:"));
				javax.swing.JTextField emailField = new javax.swing.JTextField(20);
				panel.add(emailField);
				panel.add(new javax.swing.JLabel("Senha:"));
				javax.swing.JPasswordField passField = new javax.swing.JPasswordField(20);
				panel.add(passField);

				int result = JOptionPane.showConfirmDialog(null, panel, "Login Usuário",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result != JOptionPane.OK_OPTION) {
					return null; // cancelou
				}

				String email = emailField.getText();
				String senha = new String(passField.getPassword());

				Optional<Usuarios> found = usuariosRepo.findAll().stream()
						.filter(u -> email.equals(u.getEmail()) && senha.equals(u.getSenha()))
						.findFirst();

				if (found.isPresent()) {
					return found.get().getIdUsuario();
				} else {
					int retry = JOptionPane.showConfirmDialog(null,
							"Email ou senha incorretos. Deseja tentar novamente?",
							"Login falhou", JOptionPane.YES_NO_OPTION);
					if (retry != JOptionPane.YES_OPTION) {
						return null;
					}
				}
			} // fim do while de login
		} else if (c == 1) {
			 // criar usuário com painel empilhado e validações
			while (true) {
				javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(8, 1, 5, 5));
				panel.add(new javax.swing.JLabel("Nome:"));
				javax.swing.JTextField nomeField = new javax.swing.JTextField(30);
				panel.add(nomeField);
				panel.add(new javax.swing.JLabel("Email:"));
				javax.swing.JTextField emailField = new javax.swing.JTextField(30);
				panel.add(emailField);
				panel.add(new javax.swing.JLabel("Senha (mínimo 4 caracteres):"));
				javax.swing.JPasswordField passField = new javax.swing.JPasswordField(30);
				panel.add(passField);
				panel.add(new javax.swing.JLabel("Telefone:"));
				javax.swing.JTextField telField = new javax.swing.JTextField(20);
				panel.add(telField);

				int res = JOptionPane.showConfirmDialog(null, panel, "Criar Usuário",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (res != JOptionPane.OK_OPTION) {
					return null;
				}

				final String nome = nomeField.getText() == null ? "" : nomeField.getText().trim();
				final String email = emailField.getText() == null ? "" : emailField.getText().trim();
				final String senha = new String(passField.getPassword()) == null ? "" : new String(passField.getPassword()).trim();
				final String telefone = telField.getText() == null ? "" : telField.getText().trim();

				if (nome.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Nome não pode ser vazio");
					continue;
				}
				if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
					JOptionPane.showMessageDialog(null, "Email inválido");
					continue;
				}
				boolean exists = usuariosRepo.findAll().stream()
						.anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
				if (exists) {
					JOptionPane.showMessageDialog(null, "Email já cadastrado");
					continue;
				}
				if (senha.length() < 4) {
					JOptionPane.showMessageDialog(null, "Senha muito curta");
					continue;
				}
				if (telefone.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Telefone é obrigatório");
					continue;
				}

				Usuarios u = new Usuarios();
				u.setNome(nome);
				u.setEmail(email);
				u.setSenha(senha);
				u.setTelefone(telefone);
				Usuarios saved = usuariosRepo.save(u);
				JOptionPane.showMessageDialog(null, "Usuário criado com ID: " + saved.getIdUsuario());
				return saved.getIdUsuario();
			} // fim do while de criação
		} else {
			return null;
		}
	}

    private void showUserPanel(Integer userId, MesasRepository mesasRepo, ReservasRepository reservasRepo, UsuariosRepository usuariosRepo) {
        while (true) {
            String[] opts = {"Criar Reserva", "Minhas Reservas", "Sair"};
            int c = JOptionPane.showOptionDialog(null, "Usuário ID=" + userId, "Painel Usuário",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
            if (c == 0) {
                criarReservaFluxo(userId, mesasRepo, reservasRepo);
            } else if (c == 1) {
                listarGerenciarMinhasReservas(userId, reservasRepo);
            } else {
                break;
            }
        }
    }

    private void criarReservaFluxo(Integer userId, MesasRepository mesasRepo, ReservasRepository reservasRepo) {
    try {
        // Painel vertical com espaçamento entre linhas
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(6, 1, 5, 5));

        panel.add(new javax.swing.JLabel("Data (YYYY-MM-DD):"));
        javax.swing.JTextField dataField = new javax.swing.JTextField(20);
        panel.add(dataField);

        panel.add(new javax.swing.JLabel("Horário início (HH:MM):"));
        javax.swing.JTextField inicioField = new javax.swing.JTextField(20);
        panel.add(inicioField);

        panel.add(new javax.swing.JLabel("Horário fim (HH:MM):"));
        javax.swing.JTextField fimField = new javax.swing.JTextField(20);
        panel.add(fimField);

        int result = javax.swing.JOptionPane.showConfirmDialog(
                null,
                panel,
                "Criar Reserva",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE
        );

        if (result != javax.swing.JOptionPane.OK_OPTION) return;

        // Captura e valida os dados digitados
        LocalDate data = LocalDate.parse(dataField.getText().trim());
        LocalTime inicio = LocalTime.parse(inicioField.getText().trim());
        LocalTime fim = LocalTime.parse(fimField.getText().trim());

        if (!fim.isAfter(inicio)) {
            javax.swing.JOptionPane.showMessageDialog(null, "O horário de fim deve ser maior que o de início.");
            return;
        }

        // Busca mesas disponíveis
        List<Integer> ocupadas = reservasRepo.findAll().stream()
                .filter(r -> r.getDataReserva().equals(data))
                .filter(r -> !("CANCELADA".equalsIgnoreCase(r.getStatus().toString())
                        || "CONCLUIDA".equalsIgnoreCase(r.getStatus().toString())))
                .filter(r -> timesOverlap(inicio, fim, r.getHorarioInicio(), r.getHorarioFim()))
                .map(r -> r.getIdMesa())
                .collect(Collectors.toList());

        List<com.restaurante.demo.Mesas> disponiveis = mesasRepo.findAll().stream()
                .filter(m -> !ocupadas.contains(m.getId_mesa()))
                .collect(Collectors.toList());

        if (disponiveis.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nenhuma mesa disponível para este horário.");
            return;
        }

        // Lista mesas disponíveis
        StringBuilder sb = new StringBuilder("Mesas disponíveis:\n\n");
        disponiveis.forEach(m -> sb.append("ID: ").append(m.getId_mesa())
                .append("  |  Número: ").append(m.getNumero_mesa()).append("\n"));

        String idMesaStr = javax.swing.JOptionPane.showInputDialog(sb + "\nDigite o ID da mesa desejada:");
        if (idMesaStr == null) return;
        Integer idMesa = Integer.parseInt(idMesaStr);

        Optional<com.restaurante.demo.Mesas> chosen = disponiveis.stream()
                .filter(m -> m.getId_mesa() == idMesa.intValue())
                .findFirst();

        if (chosen.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Mesa inválida.");
            return;
        }

        // Verifica novamente se ainda está livre
        boolean stillFree = reservasRepo.findAll().stream()
                .filter(r -> r.getDataReserva().equals(data))
                .filter(r -> !("CANCELADA".equalsIgnoreCase(r.getStatus().toString())
                        || "CONCLUIDA".equalsIgnoreCase(r.getStatus().toString())))
                .filter(r -> r.getIdMesa().equals(idMesa))
                .noneMatch(r -> timesOverlap(inicio, fim, r.getHorarioInicio(), r.getHorarioFim()));

        if (!stillFree) {
            javax.swing.JOptionPane.showMessageDialog(null, "Infelizmente a mesa foi reservada por outro usuário nesse intervalo.");
            return;
        }

        // Salva a reserva
        Reservas reserva = new Reservas();
        reserva.setIdMesa(idMesa);
        reserva.setIdUsuario(userId);
        reserva.setDataReserva(data);
        reserva.setHorarios(inicio, fim); // define os dois horários juntos
        reserva.setStatus(com.restaurante.demo.StatusReservaEnum.PENDENTE);
        reservasRepo.save(reserva);

        javax.swing.JOptionPane.showMessageDialog(null,
                "Reserva criada com sucesso!\nStatus: PENDENTE\nAguarde confirmação.");

    } catch (DateTimeParseException | NumberFormatException ex) {
        javax.swing.JOptionPane.showMessageDialog(null, "Entrada inválida: " + ex.getMessage());
    }
}

    private boolean timesOverlap(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private void listarGerenciarMinhasReservas(Integer userId, ReservasRepository reservasRepo) {
        List<Reservas> minhas = reservasRepo.findAll().stream()
                .filter(r -> r.getIdUsuario().equals(userId))
                .sorted(Comparator.comparing(Reservas::getDataReserva).reversed())
                .collect(Collectors.toList());

        if (minhas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Você não tem reservas.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Reservas r : minhas) {
            sb.append(String.format("ID:%d Mesa:%d Data:%s %s-%s Status:%s%n",
                    r.getIdReserva(),
                    r.getIdMesa(),
                    r.getDataReserva(),
                    r.getHorarioInicio(),
                    r.getHorarioFim(),
                    r.getStatus()));
        }
        sb.append("\nDigite ID da reserva para Gerenciar (ou vazio para voltar):");
        String sel = JOptionPane.showInputDialog(sb.toString());
        if (sel == null || sel.isBlank()) return;
        try {
            Integer id = Integer.parseInt(sel);
            Optional<Reservas> opt = reservasRepo.findById(id);
            if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Reserva não encontrada");
                return;
            }
            Reservas r = opt.get();
            String[] opts = {"Confirmar", "Cancelar", "Voltar"};
            int op = JOptionPane.showOptionDialog(null, r.toString(), "Gerenciar Reserva",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
            if (op == 0) {
                r.setStatus(com.restaurante.demo.StatusReservaEnum.CONFIRMADA);
                reservasRepo.save(r);
                JOptionPane.showMessageDialog(null, "Reserva confirmada.");
            } else if (op == 1) {
                r.setStatus(com.restaurante.demo.StatusReservaEnum.CANCELADA);
                reservasRepo.save(r);
                JOptionPane.showMessageDialog(null, "Reserva cancelada.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "ID inválido");
        }
    }

    private void showAdminPanel(MesasRepository mesasRepo, UsuariosRepository usuariosRepo, ReservasRepository reservasRepo) {
        while (true) {

            String[] opts = {"Editar Reservas", "Voltar"};
            int c = JOptionPane.showOptionDialog(
                    null,
                    "Painel do Administrador",
                    "Admin",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opts,
                    opts[0]
            );

            if (c == 0) {
                Optional<Reservas> chosen = displayReservationsAdmin(mesasRepo, usuariosRepo, reservasRepo, null);
                chosen.ifPresent(r -> handleAdminSelection(r, mesasRepo, usuariosRepo, reservasRepo));
            } else {
                break;
                }
        }
    }

	private Optional<Reservas> displayReservationsAdmin(MesasRepository mesasRepo,
                                                        UsuariosRepository usuariosRepo,
                                                        ReservasRepository reservasRepo,
                                                        java.util.function.Predicate<Reservas> filter) {
        List<Reservas> list = reservasRepo.findAll().stream()
                .filter(r -> true) // placeholder
                .filter(r -> filter == null ? true : filter.test(r))
                .sorted(Comparator.comparing(Reservas::getDataReserva).thenComparing(Reservas::getHorarioInicio))
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma reserva encontrada.");
            return Optional.empty();
        }

        javax.swing.DefaultListModel<String> model = new javax.swing.DefaultListModel<>();
        for (Reservas r : list) {
            String nome = usuariosRepo.findById(r.getIdUsuario()).map(Usuarios::getNome).orElse("?");
            String tel = usuariosRepo.findById(r.getIdUsuario()).map(Usuarios::getTelefone).orElse("?");
            String numMesa = mesasRepo.findById(r.getIdMesa()).map(m -> Integer.toString(m.getNumero_mesa())).orElse("?");
            model.addElement(String.format("ID:%d | Usuário:%s | Tel:%s | Mesa:%s | %s %s-%s | %s",
                    r.getIdReserva(), nome, tel, numMesa, r.getDataReserva(), r.getHorarioInicio(), r.getHorarioFim(), r.getStatus()));
        }

        javax.swing.JList<String> jlist = new javax.swing.JList<>(model);
        jlist.setVisibleRowCount(12);
        int sel = JOptionPane.showConfirmDialog(null, new javax.swing.JScrollPane(jlist),
                "Selecione uma reserva (clique para selecionar) e pressione OK", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (sel != JOptionPane.OK_OPTION) return Optional.empty();
        int index = jlist.getSelectedIndex();
        if (index < 0 || index >= list.size()) {
            JOptionPane.showMessageDialog(null, "Nenhuma reserva selecionada.");
            return Optional.empty();
        }
        return Optional.of(list.get(index));
    }

	private void handleAdminSelection(Reservas r, MesasRepository mesasRepo, UsuariosRepository usuariosRepo, ReservasRepository reservasRepo) {
        String nome = usuariosRepo.findById(r.getIdUsuario()).map(Usuarios::getNome).orElse("?");
        String tel = usuariosRepo.findById(r.getIdUsuario()).map(Usuarios::getTelefone).orElse("?");
        String numMesa = mesasRepo.findById(r.getIdMesa()).map(m -> Integer.toString(m.getNumero_mesa())).orElse("?");
        String details = String.format("ID:%d%nUsuário:%s%nTel:%s%nMesa:%s%nData:%s%nHorário:%s - %s%nStatus:%s",
                r.getIdReserva(), nome, tel, numMesa, r.getDataReserva(), r.getHorarioInicio(), r.getHorarioFim(), r.getStatus());

        String[] actions = {"Editar campo", "Excluir", "Voltar"};
        int a = JOptionPane.showOptionDialog(null, details, "Detalhes Reserva",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, actions, actions[0]);

        if (a == 0) {
            // Escolher campo para editar
            String[] fields = {"Data (YYYY-MM-DD)", "Horário início (HH:MM)", "Horário fim (HH:MM)", "Status", "Número da mesa (id_mesa)", "Cancelar"};
            String field = (String) JOptionPane.showInputDialog(null, "Escolha campo para editar:", "Editar",
                    JOptionPane.QUESTION_MESSAGE, null, fields, fields[0]);
            if (field == null || "Cancelar".equals(field)) return;

            try {
                switch (field) {
                    case "Data (YYYY-MM-DD)":
                        String dataStr = JOptionPane.showInputDialog("Nova data (YYYY-MM-DD):", r.getDataReserva().toString());
                        if (dataStr == null) return;
                        r.setDataReserva(java.time.LocalDate.parse(dataStr));
                        break;
                    case "Horário início (HH:MM)":
                        String inicioStr = JOptionPane.showInputDialog("Novo horário início (HH:MM):", r.getHorarioInicio().toString());
                        if (inicioStr == null) return;
                        r.setHorarioInicio(LocalTime.parse(inicioStr));
                        break;
                    case "Horário fim (HH:MM)":
                        String fimStr = JOptionPane.showInputDialog("Novo horário fim (HH:MM):", r.getHorarioFim().toString());
                        if (fimStr == null) return;
                        r.setHorarioFim(LocalTime.parse(fimStr));
                        break;
                    case "Status":
                        String[] statuses = {"PENDENTE", "CONFIRMADA", "CANCELADA", "CONCLUIDA"};
                        String novoStatus = (String) JOptionPane.showInputDialog(null, "Status:", "Status",
                                JOptionPane.QUESTION_MESSAGE, null, statuses, r.getStatus().toString());
                        if (novoStatus == null) return;
                        r.setStatus(com.restaurante.demo.StatusReservaEnum.valueOf(novoStatus));
                        break;
                    case "Número da mesa (id_mesa)":
                        String mesaIdStr = JOptionPane.showInputDialog("Novo id_mesa (integer):", r.getIdMesa().toString());
                        if (mesaIdStr == null) return;
                        int newMesaId = Integer.parseInt(mesaIdStr);
                        if (mesasRepo.findById(newMesaId).isEmpty()) {
                            JOptionPane.showMessageDialog(null, "id_mesa não existe");
                            return;
                        }
                        r.setIdMesa(newMesaId);
                        break;
                }
                // validação simples de horários
                if (!r.getHorarioFim().isAfter(r.getHorarioInicio())) {
                    JOptionPane.showMessageDialog(null, "horario_fim deve ser maior que horario_inicio. Alteração não salva.");
                    return;
                }
                reservasRepo.save(r);
                JOptionPane.showMessageDialog(null, "Reserva atualizada.");
            } catch (DateTimeParseException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Entrada inválida: " + ex.getMessage());
            }
        } else if (a == 1) {
            int del = JOptionPane.showConfirmDialog(null, "Excluir reserva ID:" + r.getIdReserva() + " ?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (del == JOptionPane.YES_OPTION) {
                reservasRepo.delete(r);
                JOptionPane.showMessageDialog(null, "Reserva excluída.");
            }
        }
    }

    private void editarReservaAdm(Reservas r, ReservasRepository reservasRepo) {
        try {
            String inicioStr = JOptionPane.showInputDialog("Horário início (HH:MM):", r.getHorarioInicio().toString());
            if (inicioStr == null) return;
            LocalTime inicio = LocalTime.parse(inicioStr);

            String fimStr = JOptionPane.showInputDialog("Horário fim (HH:MM):", r.getHorarioFim().toString());
            if (fimStr == null) return;
            LocalTime fim = LocalTime.parse(fimStr);

            if (!fim.isAfter(inicio)) {
                JOptionPane.showMessageDialog(null, "horario_fim deve ser maior que horario_inicio");
                return;
            }

            String[] statuses = {"PENDENTE", "CONFIRMADA", "CANCELADA", "CONCLUIDA"};
            String novoStatus = (String) JOptionPane.showInputDialog(null, "Status:", "Status",
                    JOptionPane.QUESTION_MESSAGE, null, statuses, r.getStatus().toString());
            r.setHorarioInicio(inicio);
            r.setHorarioFim(fim);
            r.setStatus(com.restaurante.demo.StatusReservaEnum.valueOf(novoStatus));
            reservasRepo.save(r);
            JOptionPane.showMessageDialog(null, "Reserva atualizada");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(null, "Formato inválido: " + ex.getMessage());
        }
    }
}