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

import com.example.algafood.api.assembler.output.UsuarioOutputAssembler;
import com.example.algafood.api.model.UsuarioModel;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Restaurante;
import com.example.algafood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/responsaveis")
public class RestauranteUsuarioController {

	@Autowired
	private CadastroRestauranteService service;

	@Autowired
	private UsuarioOutputAssembler usuarioAssembler;
	
	@CheckSecurity.Restaurantes.PodeConsultar
	@GetMapping
	public List<UsuarioModel> listar(@PathVariable Long restauranteId) {
		Restaurante restaurante = service.buscarOuFalhar(restauranteId);
		
		return usuarioAssembler.toCollectionDTO(restaurante.getResponsaveis());
	}

	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@DeleteMapping("/{usuarioId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void desassociar(@PathVariable Long restauranteId, @PathVariable Long usuarioId) {
		service.desassociarResponsavel(restauranteId, usuarioId);
	}
	
	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@PutMapping("/{usuarioId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void associar(@PathVariable Long restauranteId, @PathVariable Long usuarioId) {
		service.associarResponsavel(restauranteId, usuarioId);
	}

}
