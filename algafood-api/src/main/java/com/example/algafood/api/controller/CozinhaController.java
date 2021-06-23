package com.example.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algafood.api.assembler.input.CozinhaInputDisassembler;
import com.example.algafood.api.assembler.output.CozinhaOutputAssembler;
import com.example.algafood.api.model.CozinhaModel;
import com.example.algafood.api.model.input.CozinhaInput;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Cozinha;
import com.example.algafood.domain.repository.CozinhaRepository;
import com.example.algafood.domain.service.CadastroCozinhaService;

@RestController
@RequestMapping("/cozinhas")
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;

	@Autowired
	private CadastroCozinhaService service;

	@Autowired
	private CozinhaOutputAssembler cozinhaAssembler;

	@Autowired
	private CozinhaInputDisassembler cozinhaDisassembler;       

	@CheckSecurity.Cozinhas.PodeConsultar
	@GetMapping
	public Page<CozinhaModel> listar(@PageableDefault(size=10) Pageable pageable) {
//		System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		Page<Cozinha> cozinhasPage = cozinhaRepository.findAll(pageable);
		
		List<CozinhaModel> cozinhasModel = cozinhaAssembler.toCollectionDTO(cozinhasPage.getContent());
		
		return new PageImpl<>(cozinhasModel, pageable, cozinhasPage.getTotalElements());		
	}

	@CheckSecurity.Cozinhas.PodeConsultar
	@GetMapping("/{id}")
	public CozinhaModel buscarPorId(@PathVariable Long id) {
		Cozinha cozinha = service.buscarOuFalhar(id);
		
		return cozinhaAssembler.toDTO(cozinha);
	}

	@CheckSecurity.Cozinhas.PodeEditar
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaModel adicionar(@RequestBody @Valid CozinhaInput cozinhaInput) {
		Cozinha cozinha = cozinhaDisassembler.toDomain(cozinhaInput);
		cozinha = service.salvar(cozinha);
		return cozinhaAssembler.toDTO(cozinha);
	}
	
	@CheckSecurity.Cozinhas.PodeEditar
	@PutMapping("/{id}")
	public CozinhaModel atualizar(@PathVariable Long id, @RequestBody @Valid CozinhaInput cozinhaInput) {

		Cozinha cozinhaAtual = service.buscarOuFalhar(id);

		cozinhaDisassembler.copyToOutput(cozinhaInput, cozinhaAtual);

		cozinhaAtual = service.salvar(cozinhaAtual);
		
		return cozinhaAssembler.toDTO(cozinhaAtual);
	}

	@CheckSecurity.Cozinhas.PodeEditar
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}

}
