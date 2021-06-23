package com.example.algafood.domain.repository;

import java.util.Optional;

import com.example.algafood.domain.model.Usuario;

public interface UsuarioRepository extends CustomJpaRepository<Usuario, Long> {
		
	Optional<Usuario> findByEmail(String email);
}
