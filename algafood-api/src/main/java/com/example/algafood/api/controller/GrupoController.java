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

import com.example.algafood.api.assembler.input.GrupoInputDisassembler;
import com.example.algafood.api.assembler.output.GrupoOutputAssembler;
import com.example.algafood.api.model.GrupoModel;
import com.example.algafood.api.model.input.GrupoInput;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Grupo;
import com.example.algafood.domain.repository.GrupoRepository;
import com.example.algafood.domain.service.CadastroGrupoService;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private CadastroGrupoService service;

	@Autowired
	private GrupoOutputAssembler grupoAssembler;

	@Autowired
	private GrupoInputDisassembler grupoDisassembler;

	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@GetMapping
	public ResponseEntity<List<GrupoModel>> listar() {
		return ResponseEntity.ok(grupoAssembler.toCollectionDTO(grupoRepository.findAll()));
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@GetMapping("/{id}")
	public GrupoModel buscarPorId(@PathVariable Long id) {
		Grupo grupo =  service.buscarOuFalhar(id);
		
		return grupoAssembler.toDTO(grupo);
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GrupoModel adicionar(@RequestBody @Valid GrupoInput grupoInput) {
		Grupo grupo = grupoDisassembler.toDomain(grupoInput);

		return grupoAssembler.toDTO(service.salvar(grupo));
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@PutMapping("/{id}")
	public GrupoModel atualizar(@PathVariable Long id, @RequestBody @Valid GrupoInput grupoInput) {

		Grupo grupoAtual = service.buscarOuFalhar(id);
		grupoDisassembler.copyToOutput(grupoInput, grupoAtual);
		grupoAtual = service.salvar(grupoAtual);
		
		return grupoAssembler.toDTO(grupoAtual);
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}
}
