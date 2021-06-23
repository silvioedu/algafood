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

import com.example.algafood.api.assembler.output.PermissaoOutputAssembler;
import com.example.algafood.api.model.PermissaoModel;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Grupo;
import com.example.algafood.domain.service.CadastroGrupoService;

@RestController
@RequestMapping("/grupos/{grupoId}/permissoes")
public class GrupoPermissaoController {

	@Autowired
	private CadastroGrupoService service;
	
	@Autowired
	private PermissaoOutputAssembler permissaoAssembler;
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@GetMapping
	public List<PermissaoModel> listar(@PathVariable Long grupoId){
		Grupo grupo = service.buscarOuFalhar(grupoId);
		return permissaoAssembler.toCollectionDTO(grupo.getPermissoes());
	}
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@PutMapping("/{permissaoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void associar(@PathVariable long grupoId, @PathVariable Long permissaoId) {
		service.associarPermissao(grupoId, permissaoId);
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@DeleteMapping("/{permissaoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void desassociar(@PathVariable long grupoId, @PathVariable Long permissaoId) {
		service.desassociarPermissao(grupoId, permissaoId);
	}
}
