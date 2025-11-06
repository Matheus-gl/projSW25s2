// ...existing code...
package com.restaurante.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mesas")
public class Mesas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private int id_mesa;

    @Column(name = "numero_mesa", nullable = false, unique = true)
    private int numero_mesa;

    @Column(name = "capacidade", nullable = false)
    private int capacidade;

    @Convert(converter = StatusMesaConverter.class)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('disponivel','reservada','inativa')")
    private StatusMesaEnum status = StatusMesaEnum.DISPONIVEL; // Valor padrão

    public Mesas() {
        // default já define estado como DISPONIVEL
    }

    public Mesas(int id_mesa, int numero_mesa, int capacidade, StatusMesaEnum status) {
        this.id_mesa = id_mesa;
        this.numero_mesa = numero_mesa;
        this.capacidade = capacidade;
        this.status = (status != null) ? status : StatusMesaEnum.DISPONIVEL;
    }

    // getters e setters
    public int getId_mesa() {
        return id_mesa;
    }

    public void setId_mesa(int id_mesa) {
        this.id_mesa = id_mesa;
    }

    public int getNumero_mesa() {
        return numero_mesa;
    }

    public void setNumero_mesa(int numero_mesa) {
        this.numero_mesa = numero_mesa;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public StatusMesaEnum getStatus() {
        return status;
    }

    public void setStatus(StatusMesaEnum status) {
        this.status = (status != null) ? status : StatusMesaEnum.DISPONIVEL;
    }

    @Override
    public String toString() {
        return "Mesas{" +
                "id_mesa=" + id_mesa +
                ", numero_mesa=" + numero_mesa +
                ", capacidade=" + capacidade +
                ", status=" + status +
                '}';
    }
}