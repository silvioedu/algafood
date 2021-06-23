package com.example.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algafood.api.assembler.input.FormaPagamentoInputDisassembler;
import com.example.algafood.api.assembler.output.FormaPagamentoOutputAssembler;
import com.example.algafood.api.model.FormaPagamentoModel;
import com.example.algafood.api.model.input.FormaPagamentoInput;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.FormaPagamento;
import com.example.algafood.domain.repository.FormaPagamentoRepository;
import com.example.algafood.domain.service.CadastroFormaPagamentoService;

@RestController
@RequestMapping("/formas-pagamento")
public class FormaPagamentoController {

	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;

	@Autowired
	private CadastroFormaPagamentoService service;

	@Autowired
	private FormaPagamentoOutputAssembler formaPagamentoAssembler;

	@Autowired
	private FormaPagamentoInputDisassembler formaPagamentoDisassembler;

	@CheckSecurity.FormasPagamento.PodeConsultar
	@GetMapping
	public ResponseEntity<List<FormaPagamentoModel>> listar() {
		return ResponseEntity.ok(formaPagamentoAssembler.toCollectionDTO(formaPagamentoRepository.findAll()));
	}

	@CheckSecurity.FormasPagamento.PodeConsultar
	@GetMapping("/{id}")
	public FormaPagamentoModel buscarPorId(@PathVariable Long id) {
		FormaPagamento formaPagamento = service.buscarOuFalhar(id);
		return formaPagamentoAssembler.toDTO(formaPagamento);
	}

	@CheckSecurity.FormasPagamento.PodeEditar
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FormaPagamentoModel adicionar(@RequestBody @Valid FormaPagamentoInput formaPagamentoInput) {
		FormaPagamento formaPagamento = formaPagamentoDisassembler.toDomain(formaPagamentoInput);
		formaPagamento = service.salvar(formaPagamento);
		return formaPagamentoAssembler.toDTO(formaPagamento);
	}

	@CheckSecurity.FormasPagamento.PodeEditar
	@PutMapping("/{id}")
	public FormaPagamentoModel atualizar(@PathVariable Long id, @RequestBody FormaPagamentoInput formaPagamentoInput) {
		FormaPagamento formaPagamentoAtual = service.buscarOuFalhar(id);
		formaPagamentoDisassembler.copyToOutput(formaPagamentoInput, formaPagamentoAtual);
		formaPagamentoAtual = service.salvar(formaPagamentoAtual);
		return formaPagamentoAssembler.toDTO(formaPagamentoAtual);
	}

	@CheckSecurity.FormasPagamento.PodeEditar
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}

}
