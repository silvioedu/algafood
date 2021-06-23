package com.example.algafood.domain.repository;

import org.springframework.stereotype.Repository;

import com.example.algafood.domain.model.Permissao;

@Repository
public interface PermissaoRepository extends CustomJpaRepository<Permissao, Long>{

}
