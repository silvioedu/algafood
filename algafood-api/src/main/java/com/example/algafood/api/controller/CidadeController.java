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

import com.example.algafood.api.assembler.input.CidadeInputDisassembler;
import com.example.algafood.api.assembler.output.CidadeOutputAssembler;
import com.example.algafood.api.model.CidadeModel;
import com.example.algafood.api.model.input.CidadeInput;
import com.example.algafood.api.util.ResourceUriHelper;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.exception.EstadoNaoEncontradoException;
import com.example.algafood.domain.exception.NegocioException;
import com.example.algafood.domain.model.Cidade;
import com.example.algafood.domain.repository.CidadeRepository;
import com.example.algafood.domain.service.CadastroCidadeService;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private CadastroCidadeService service;
	
	@Autowired
	private CidadeOutputAssembler cidadeAssembler;

	@Autowired
	private CidadeInputDisassembler cidadeDisassembler;       

	@CheckSecurity.Cidades.PodeConsultar
	@GetMapping
	public ResponseEntity<List<CidadeModel>> listar() {
		return ResponseEntity.ok(cidadeAssembler.toCollectionDTO(cidadeRepository.findAll()));
	}

	@CheckSecurity.Cidades.PodeConsultar
	@GetMapping("/{id}")
	public CidadeModel buscarPorId(@PathVariable Long id) {
		return cidadeAssembler.toDTO(service.buscarOuFalhar(id));
	}

	@CheckSecurity.Cidades.PodeEditar
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeModel adicionar(@RequestBody @Valid CidadeInput cidadeInput) {
		try {
			Cidade cidade = cidadeDisassembler.toDomain(cidadeInput);
			cidade = service.salvar(cidade);
			CidadeModel cidadeModel =  cidadeAssembler.toDTO(cidade);
			
			ResourceUriHelper.addUriInResponseHeader(cidadeModel.getId());
			return cidadeModel;
		} catch (EstadoNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@CheckSecurity.Cidades.PodeEditar
	@PutMapping("/{id}")
	public CidadeModel atualizar(@PathVariable Long id, @RequestBody @Valid CidadeInput cidadeInput) {
		try {
			Cidade cidadeAtual = service.buscarOuFalhar(id);
			
			cidadeDisassembler.copyToOutput(cidadeInput, cidadeAtual);

			cidadeAtual = service.salvar(cidadeAtual);
			
			return cidadeAssembler.toDTO(cidadeAtual);
		} catch (EstadoNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@CheckSecurity.Cidades.PodeEditar
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}
}
