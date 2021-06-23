package com.example.algafood.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algafood.api.assembler.output.FormaPagamentoOutputAssembler;
import com.example.algafood.api.model.FormaPagamentoModel;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Restaurante;
import com.example.algafood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/formas-pagamento")
public class RestauranteFormaPagamentoController {

	@Autowired
	private CadastroRestauranteService service;
	
	@Autowired
	private FormaPagamentoOutputAssembler formaPagamentoAssembler;
	
	@CheckSecurity.Restaurantes.PodeConsultar
	@GetMapping
	public List<FormaPagamentoModel> listar(@PathVariable Long restauranteId) {
		Restaurante restaurante = service.buscarOuFalhar(restauranteId);
		
		return formaPagamentoAssembler.toCollectionDTO(restaurante.getFormasPagamento());
	}
	
	@CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
	@DeleteMapping("/{formaPagamentoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void desassociar(@PathVariable Long restauranteId, @PathVariable Long formaPagamentoId) {
		service.desassociarFormaPagamento(restauranteId, formaPagamentoId);
	}
	
	@CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
	@PutMapping("/{formaPagamentoId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void associar(@PathVariable Long restauranteId, @PathVariable Long formaPagamentoId) {
		service.associarFormaPagamento(restauranteId, formaPagamentoId);
	}

}
