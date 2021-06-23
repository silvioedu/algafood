package com.example.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.algafood.api.assembler.input.RestauranteInputDisassembler;
import com.example.algafood.api.assembler.output.RestauranteOutputAssembler;
import com.example.algafood.api.model.RestauranteModel;
import com.example.algafood.api.model.input.RestauranteInput;
import com.example.algafood.api.model.view.RestauranteView;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.exception.CidadeNaoEncontradaException;
import com.example.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.example.algafood.domain.exception.NegocioException;
import com.example.algafood.domain.exception.RestauranteNaoEncontradoException;
import com.example.algafood.domain.model.Restaurante;
import com.example.algafood.domain.repository.RestauranteRepository;
import com.example.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroRestauranteService service;

	@Autowired
	private RestauranteOutputAssembler restauranteAssembler;

	@Autowired
	private RestauranteInputDisassembler restauranteDisassembler;

//	@Autowired
//	private SmartValidator validator;

	@CheckSecurity.Restaurantes.PodeConsultar
	@JsonView(RestauranteView.Resumo.class)
	@GetMapping
	public List<RestauranteModel> listar(){
		return restauranteAssembler.toCollectionDTO(restauranteRepository.findAll());
	}

	@CheckSecurity.Restaurantes.PodeConsultar
	@JsonView(RestauranteView.ApenasNome.class)
	@GetMapping(params = "projecao=apenas-nome")
	public List<RestauranteModel> listarApenasNomes(){
		return restauranteAssembler.toCollectionDTO(restauranteRepository.findAll());
	}

//	@GetMapping
//	public MappingJacksonValue listar(@RequestParam(required=false) String projecao) {
//		List<Restaurante> restaurantes = restauranteRepository.findAll();
//		List<RestauranteModel> restaurantesModel = restauranteAssembler.toCollectionDTO(restaurantes);
//		
//		MappingJacksonValue wrapper = new MappingJacksonValue(restaurantesModel);
//		wrapper.setSerializationView(RestauranteView.Resumo.class);
//		
//		if ("apenas-nome".equals(projecao)) {
//			wrapper.setSerializationView(RestauranteView.ApenasNome.class);
//		} else if("completo".equals(projecao)) {
//			wrapper.setSerializationView(null);			
//		}
//		return wrapper;
//	}

//	@JsonView(RestauranteView.Resumo.class)
//	@GetMapping(params = "projecao=resumo")
//	public ResponseEntity<List<RestauranteModel>> listarResumido() {
//		return listar();
//	}
//	
//	@JsonView(RestauranteView.ApenasNome.class)
//	@GetMapping(params = "projecao=apenas-nome")
//	public ResponseEntity<List<RestauranteModel>> listarApenasNomes() {
//		return listar();
//	}

	@CheckSecurity.Restaurantes.PodeConsultar
	@GetMapping("/{id}")
	public RestauranteModel buscarPorId(@PathVariable Long id) {
		Restaurante restaurante = service.buscarOuFalhar(id);

		return restauranteAssembler.toDTO(restaurante);
	}

	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteModel adicionar(@RequestBody @Valid RestauranteInput restauranteInput) {
		try {
			Restaurante restaurante = restauranteDisassembler.toDomain(restauranteInput);
			return restauranteAssembler.toDTO(service.salvar(restaurante));
		} catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}

	}

	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@PutMapping("/{id}")
	public RestauranteModel atualizar(@PathVariable Long id, @RequestBody @Valid RestauranteInput restauranteInput) {

		Restaurante restauranteAtual = service.buscarOuFalhar(id);
		restauranteDisassembler.copyToOutput(restauranteInput, restauranteAtual);

		try {
			return restauranteAssembler.toDTO(service.salvar(restauranteAtual));
		} catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		service.excluir(id);
	}

	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@PutMapping("/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativar(@PathVariable Long id) {
		service.ativar(id);
	}

	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@PutMapping("/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativarMultiplos(@RequestBody List<Long> ids) {
		try {
			service.ativar(ids);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@CheckSecurity.Restaurantes.PodeGerenciarCadastro
	@DeleteMapping("/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativarMultiplos(@RequestBody List<Long> ids) {
		try {
			service.inativar(ids);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
	@PutMapping("/{id}/abertura")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void abrir(@PathVariable Long id) {
		service.abrir(id);
	}

	@CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
	@PutMapping("/{id}/fechamento")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void fechar(@PathVariable Long id) {
		service.fechar(id);
	}

//	@GetMapping("/por-nome-e-frete")
//	public List<Restaurante> listarPorNomeFrete(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal) {
//		return restauranteRepository.find(nome, taxaFreteInicial, taxaFreteFinal);
//	}
//
//	@GetMapping("/com-frete-gratis")
//	public List<Restaurante> listarComFreteGratis(String nome) {
//		return restauranteRepository.findComFreteGratis(nome);
//	}
//
//	@GetMapping("/primeiro")
//	public Optional<Restaurante> buscarPrimeiro() {
//		return restauranteRepository.buscarPrimeiro();
//	}
//
//	@PatchMapping("/{id}")
//	public Restaurante atualizarParcial(@PathVariable Long id,
//			@RequestBody Map<String, Object> campos, HttpServletRequest request) {
//		Restaurante restauranteAtual = service.buscarOuFalhar(id);
//		
//		merge(campos, restauranteAtual, request);
//		validate(restauranteAtual, "restaurante");
//		
//		return atualizar(id, restauranteAtual);
//	}
//
//	private void validate(Restaurante restaurante, String objectName) {
//		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restaurante, objectName);
//		validator.validate(restaurante, bindingResult);
//		
//		if (bindingResult.hasErrors()) {
//			throw new ValidacaoException(bindingResult);
//		}
//	}
//
//	private void merge(Map<String, Object> dadosOrigem, Restaurante restauranteDestino,
//			HttpServletRequest request) {
//		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
//		
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
//			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
//			
//			Restaurante restauranteOrigem = objectMapper.convertValue(dadosOrigem, Restaurante.class);
//			
//			dadosOrigem.forEach((nomePropriedade, valorPropriedade) -> {
//				Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
//				field.setAccessible(true);
//				
//				Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);
//				
//				ReflectionUtils.setField(field, restauranteDestino, novoValor);
//			});
//		} catch (IllegalArgumentException e) {
//			Throwable rootCause = ExceptionUtils.getRootCause(e);
//			throw new HttpMessageNotReadableException(e.getMessage(), rootCause, serverHttpRequest);
//		}
//	}

}
