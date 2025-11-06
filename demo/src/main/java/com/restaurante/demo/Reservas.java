
package com.restaurante.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "reservas")
@Check(constraints = "horario_fim > horario_inicio")
public class Reservas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "id_mesa", nullable = false)
    private Integer idMesa;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "data_reserva", nullable = false)
    private LocalDate dataReserva;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fim", nullable = false)
    private LocalTime horarioFim;

    @Convert(converter = StatusReservaConverter.class)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('pendente','confirmada','cancelada','concluida') DEFAULT 'pendente'")
    private StatusReservaEnum status = StatusReservaEnum.PENDENTE;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    public Reservas() { }

    public Reservas(Integer idReserva, Integer idMesa, Integer idUsuario, LocalDate dataReserva,
                    LocalTime horarioInicio, LocalTime horarioFim, StatusReservaEnum status,
                    LocalDateTime dataCriacao) {
        this.idReserva = idReserva;
        this.idMesa = idMesa;
        this.idUsuario = idUsuario;
        this.dataReserva = dataReserva;
        setHorarios(horarioInicio, horarioFim);
        this.status = (status != null) ? status : StatusReservaEnum.PENDENTE;
        this.dataCriacao = (dataCriacao != null) ? dataCriacao : null;
    }

    public Reservas(Integer idMesa, Integer idUsuario, LocalDate dataReserva,
                    LocalTime horarioInicio, LocalTime horarioFim) {
        this(null, idMesa, idUsuario, dataReserva, horarioInicio, horarioFim, StatusReservaEnum.PENDENTE, null);
    }

    private void setHorarios(LocalTime inicio, LocalTime fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("horarioInicio e horarioFim não podem ser nulos");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("horario_fim deve ser maior que horario_inicio");
        }
        this.horarioInicio = inicio;
        this.horarioFim = fim;
    }

    // getters e setters
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public Integer getIdMesa() { return idMesa; }
    public void setIdMesa(Integer idMesa) { this.idMesa = idMesa; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }

    public LocalTime getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(LocalTime horarioInicio) { setHorarios(horarioInicio, this.horarioFim); }

    public LocalTime getHorarioFim() { return horarioFim; }
    public void setHorarioFim(LocalTime horarioFim) { setHorarios(this.horarioInicio, horarioFim); }

    public StatusReservaEnum getStatus() { return status; }
    public void setStatus(StatusReservaEnum status) { this.status = (status != null) ? status : StatusReservaEnum.PENDENTE; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    @Override
    public String toString() {
        return "Reservas{" +
                "idReserva=" + idReserva +
                ", idMesa=" + idMesa +
                ", idUsuario=" + idUsuario +
                ", dataReserva=" + dataReserva +
                ", horarioInicio=" + horarioInicio +
                ", horarioFim=" + horarioFim +
                ", status=" + status +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}