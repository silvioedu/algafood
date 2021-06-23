package com.example.algafood.domain.repository;

import org.springframework.stereotype.Repository;

import com.example.algafood.domain.model.FormaPagamento;

@Repository
public interface FormaPagamentoRepository extends CustomJpaRepository<FormaPagamento, Long> {
	
}
