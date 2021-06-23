package com.example.algafoodauth.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algafoodauth.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
		
	Optional<Usuario> findByEmail(String email);
}
