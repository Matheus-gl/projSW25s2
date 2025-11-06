
package com.restaurante.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.demo.Reservas;

public interface ReservasRepository extends JpaRepository<Reservas, Integer> {
}