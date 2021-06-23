package com.example.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algafood.api.assembler.input.ProdutoInputDisassembler;
import com.example.algafood.api.assembler.output.ProdutoOutputAssembler;
import com.example.algafood.api.model.ProdutoModel;
import com.example.algafood.api.model.input.ProdutoInput;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Produto;
import com.example.algafood.domain.model.Restaurante;
import com.example.algafood.domain.repository.ProdutoRepository;
import com.example.algafood.domain.service.CadastroProdutoService;
import com.example.algafood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/produtos")
public class RestauranteProdutoController {

	@Autowired
	private CadastroProdutoService service;
	
	@Autowired
	private CadastroRestauranteService restauranteService;

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ProdutoOutputAssembler produtoAssembler;

	@Autowired
	private ProdutoInputDisassembler produtoDisassembler;

	@CheckSecurity.Restaurantes.PodeConsultar
	@GetMapping
	public List<ProdutoModel> listar(@PathVariable Long restauranteId, @RequestParam(required=false) boolean incluirInativos) {
		Restaurante restaurante = restauranteService.buscarOuFalhar(restauranteId);
		
		List<Produto> produtos = null;
		
		if (incluirInativos) {
			produtos =  produtoRepository.findTodosByRestaurante(restaurante);
			
		} else {
			produtos =  produtoRepository.findAtivosByRestaurante(restaurante);			
		}
		
		return produtoAssembler.toCollectionDTO(produtos);
	}

	@CheckSecurity.Restaurantes.PodeConsultar
	@GetMapping("/{produtoId}")
	public ProdutoModel buscarPorId(@PathVariable Long restauranteId, @PathVariable Long produtoId) {
		Produto produto = service.buscarOuFalhar(restauranteId, produtoId);

		return produtoAssembler.toDTO(produto);
	}

	@CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProdutoModel adicionar(@PathVariable Long restauranteId, @RequestBody @Valid ProdutoInput produtoInput) {
		Restaurante restaurante = restauranteService.buscarOuFalhar(restauranteId);

		Produto produto = produtoDisassembler.toDomain(produtoInput);
		produto.setRestaurante(restaurante);

		return produtoAssembler.toDTO(service.salvar(produto));
	}
}
