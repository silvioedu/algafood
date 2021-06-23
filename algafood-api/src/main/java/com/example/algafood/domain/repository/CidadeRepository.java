package com.example.algafood.domain.repository;

import org.springframework.stereotype.Repository;

import com.example.algafood.domain.model.Cidade;

@Repository
public interface CidadeRepository extends CustomJpaRepository<Cidade, Long> {

}
