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

import com.example.algafood.api.assembler.input.UsuarioInputDisassembler;
import com.example.algafood.api.assembler.output.UsuarioOutputAssembler;
import com.example.algafood.api.model.UsuarioModel;
import com.example.algafood.api.model.input.SenhaInput;
import com.example.algafood.api.model.input.UsuarioComSenhaInput;
import com.example.algafood.api.model.input.UsuarioInput;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Usuario;
import com.example.algafood.domain.repository.UsuarioRepository;
import com.example.algafood.domain.service.CadastroUsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CadastroUsuarioService service;

	@Autowired
	private UsuarioOutputAssembler usuarioAssembler;

	@Autowired
	private UsuarioInputDisassembler usuarioDisassembler;

	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@GetMapping
	public ResponseEntity<List<UsuarioModel>> listar() {
		return ResponseEntity.ok(usuarioAssembler.toCollectionDTO(usuarioRepository.findAll()));
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@GetMapping("/{id}")
	public UsuarioModel buscarPorId(@PathVariable Long id) {
		Usuario usuario =  service.buscarOuFalhar(id);
		
		return usuarioAssembler.toDTO(usuario);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioModel adicionar(@RequestBody @Valid UsuarioComSenhaInput usuarioInput) {
		Usuario usuario = usuarioDisassembler.toDomain(usuarioInput);

		return usuarioAssembler.toDTO(service.salvar(usuario));
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeAlterarUsuario
	@PutMapping("/{id}")
	public UsuarioModel atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioInput usuarioInput) {

		Usuario usuarioAtual = service.buscarOuFalhar(id);
		usuarioDisassembler.copyToOutput(usuarioInput, usuarioAtual);
		usuarioAtual = service.salvar(usuarioAtual);
		
		return usuarioAssembler.toDTO(usuarioAtual);
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeAlterarPropriaSenha
	@PutMapping("/{id}/senha")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizar(@PathVariable Long id, @RequestBody @Valid SenhaInput senhaInput) {
		service.alterarSenha(id, senhaInput.getSenhaAtual(), senhaInput.getNovaSenha());
	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}
}
