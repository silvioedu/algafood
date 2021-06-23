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

import com.example.algafood.api.assembler.input.PermissaoInputDisassembler;
import com.example.algafood.api.assembler.output.PermissaoOutputAssembler;
import com.example.algafood.api.model.PermissaoModel;
import com.example.algafood.api.model.input.PermissaoInput;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Permissao;
import com.example.algafood.domain.repository.PermissaoRepository;
import com.example.algafood.domain.service.CadastroPermissaoService;

@RestController
@RequestMapping("/permissoes")
public class PermissaoController {

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private CadastroPermissaoService service;
	
	@Autowired
	private PermissaoOutputAssembler permissaoAssembler;

	@Autowired
	private PermissaoInputDisassembler permissaoDisassembler;

	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@GetMapping
	public ResponseEntity<List<PermissaoModel>> listar() {
		return ResponseEntity.ok(permissaoAssembler.toCollectionDTO(permissaoRepository.findAll()));
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@GetMapping("/{id}")
	public PermissaoModel buscarPorId(@PathVariable Long id) {
		Permissao permissao = service.buscarOuFalhar(id);
		return permissaoAssembler.toDTO(permissao);
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PermissaoModel adicionar(@RequestBody @Valid PermissaoInput permissaoInput) {
		Permissao permissao = permissaoDisassembler.toDomain(permissaoInput);
		permissao = service.salvar(permissao);
		return permissaoAssembler.toDTO(permissao);
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@PutMapping("/{id}")
	public PermissaoModel atualizar(@PathVariable Long id, @RequestBody PermissaoInput permissaoInput) {
		Permissao permissaoAtual = service.buscarOuFalhar(id);
		permissaoDisassembler.copyToOutput(permissaoInput, permissaoAtual);
		permissaoAtual = service.salvar(permissaoAtual);
		return permissaoAssembler.toDTO(permissaoAtual);
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}
}
