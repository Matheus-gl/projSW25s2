package com.restaurante.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.demo.Mesas;

public interface MesasRepository extends JpaRepository<Mesas, Integer> {
}