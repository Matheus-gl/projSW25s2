package com.restaurante.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.demo.Usuarios;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
}