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

import com.example.algafood.api.assembler.input.EstadoInputDisassembler;
import com.example.algafood.api.assembler.output.EstadoOutputAssembler;
import com.example.algafood.api.model.EstadoModel;
import com.example.algafood.api.model.input.EstadoInput;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Estado;
import com.example.algafood.domain.repository.EstadoRepository;
import com.example.algafood.domain.service.CadastroEstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {

	@Autowired
	private EstadoRepository estadoRepository;

	@Autowired
	private CadastroEstadoService service;

	@Autowired
	private EstadoOutputAssembler estadoAssembler;

	@Autowired
	private EstadoInputDisassembler estadoDisassembler;

	@CheckSecurity.Estados.PodeConsultar
	@GetMapping
	public ResponseEntity<List<EstadoModel>> listar() {
		return ResponseEntity.ok(estadoAssembler.toCollectionDTO(estadoRepository.findAll()));
	}

	@CheckSecurity.Estados.PodeConsultar
	@GetMapping("/{id}")
	public EstadoModel buscarPorId(@PathVariable Long id) {
		Estado estado =  service.buscarOuFalhar(id);
		
		return estadoAssembler.toDTO(estado);
	}

	@CheckSecurity.Estados.PodeEditar
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoModel adicionar(@RequestBody @Valid EstadoInput estadoInput) {
		Estado estado = estadoDisassembler.toDomain(estadoInput);

		return estadoAssembler.toDTO(service.salvar(estado));
	}

	@CheckSecurity.Estados.PodeEditar
	@PutMapping("/{id}")
	public EstadoModel atualizar(@PathVariable Long id, @RequestBody @Valid EstadoInput estadoInput) {

		Estado estadoAtual = service.buscarOuFalhar(id);
		estadoDisassembler.copyToOutput(estadoInput, estadoAtual);
		estadoAtual = service.salvar(estadoAtual);
		
		return estadoAssembler.toDTO(estadoAtual);
	}

	@CheckSecurity.Estados.PodeEditar
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}
}
