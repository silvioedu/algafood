package com.example.algafood.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.example.algafood.domain.exception.EntidadeEmUsoException;
import com.example.algafood.domain.model.Cozinha;

@SpringBootTest
class CadastroCozinhaServiceIT {

	@Autowired
	private CadastroCozinhaService service;
	
	void deveAtribuirId_QuandoCadastrarCozinhaComDadosCorretos() {
		
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome("Chinesa");
		
		novaCozinha = service.salvar(novaCozinha);
		
		assertThat(novaCozinha).isNotNull();
		assertThat(novaCozinha.getId()).isNotNull();
	}
	
	void deveExcluir_QuandoCozinhaNaoEstaEmUso() {
		
		service.excluir(5L);
		
	}
	
	void deveFalhar_QuandoCadastrarCozinhaSemNome() {
		
		try {
			
			Cozinha novaCozinha = new Cozinha();
			novaCozinha.setNome(null);
			
			novaCozinha = service.salvar(novaCozinha);
			
		} catch (Exception e) {
			assertThat(e).isInstanceOf(ConstraintViolationException.class);
		}			
	}
	
	void deveFalhar_QuandoExcluirCozinhaEmUso() {
		
		try {
			service.excluir(1L);
		} catch (Exception e) {
			assertThat(e).isInstanceOf(EntidadeEmUsoException.class);
		}
	}
	
	void deveFalhar_quandoExcluirCozinhaInexistente() {
		try {
			service.excluir(100L);
		} catch (Exception e) {
			assertThat(e).isInstanceOf(CozinhaNaoEncontradaException.class);
		}
		
	}
	
}
