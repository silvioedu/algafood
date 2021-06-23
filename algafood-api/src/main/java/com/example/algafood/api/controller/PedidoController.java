package com.example.algafood.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algafood.api.assembler.input.PedidoInputDisassembler;
import com.example.algafood.api.assembler.output.PedidoOutputAssembler;
import com.example.algafood.api.assembler.output.PedidoResumoOutputAssembler;
import com.example.algafood.api.model.PedidoModel;
import com.example.algafood.api.model.PedidoResumoModel;
import com.example.algafood.api.model.input.PedidoInput;
import com.example.algafood.api.model.input.filter.PedidoFilter;
import com.example.algafood.core.data.PageableTranslator;
import com.example.algafood.core.security.AlgaSecurity;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.model.Pedido;
import com.example.algafood.domain.model.Usuario;
import com.example.algafood.domain.repository.PedidoRepository;
import com.example.algafood.domain.service.CadastroPedidoService;
import com.example.algafood.infrastructure.repository.spec.PedidoSpecs;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	@Autowired
	private CadastroPedidoService service;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PedidoResumoOutputAssembler pedidoResumoAssembler;

	@Autowired
	private PedidoOutputAssembler pedidoAssembler;
	
	@Autowired
	private PedidoInputDisassembler pedidoInputDisassembler;
	
	@Autowired
	private AlgaSecurity algaSecurity;

//	@GetMapping
//	public MappingJacksonValue listar(@RequestParam(required=false) String campos) {
//		List<Pedido> pedidos = pedidoRepository.findAll();
//		List<PedidoResumoModel> pedidosModel = pedidoResumoAssembler.toCollectionDTO(pedidos);
//		
//		MappingJacksonValue wrapper = new MappingJacksonValue(pedidosModel);
//		
//		SimpleFilterProvider filter = new SimpleFilterProvider();
//		filter.addFilter("pedidoFilter", SimpleBeanPropertyFilter.serializeAll());
//		
//		if (Objects.nonNull(campos)) {
//			filter.addFilter("pedidoFilter", SimpleBeanPropertyFilter.filterOutAllExcept(campos.split(",")));
//		}
//		
//		wrapper.setFilters(filter);
//		
//		return wrapper;
//	}

	@CheckSecurity.Pedidos.PodePesquisar
	@GetMapping
	public Page<PedidoResumoModel> pesquisar(PedidoFilter filtro, @PageableDefault(size=10) Pageable  pageable) {
		
		pageable = traduzirPageable(pageable);
		
		Page<Pedido> pedidosPage = pedidoRepository.findAll(PedidoSpecs.usandoFiltro(filtro), pageable);
		List<PedidoResumoModel> pedidosModel = pedidoResumoAssembler.toCollectionDTO(pedidosPage.getContent());
		
		return new PageImpl<>(pedidosModel, pageable, pedidosPage.getTotalElements());
	}

	@CheckSecurity.Pedidos.PodeBuscar
	@GetMapping("/{codigo}")
	public PedidoModel buscarPorCodigo(@PathVariable String codigo) {
		Pedido pedido = service.buscarOuFalhar(codigo);
		return pedidoAssembler.toDTO(pedido);
	}
	
	@CheckSecurity.Pedidos.PodeCriar
	@PostMapping
	public PedidoModel adicionar(@RequestBody PedidoInput pedidoInput) {
        Pedido pedido = pedidoInputDisassembler.toDomain(pedidoInput);
		
        // TODO pegar usuário autenticado
        pedido.setCliente(new Usuario());
        pedido.getCliente().setId(algaSecurity.getUsuarioId());
        
        pedido = service.emitir(pedido);
        
        return pedidoAssembler.toDTO(pedido);
	}
	
	@CheckSecurity.Pedidos.PodeGerenciarPedidos
	@PutMapping("/{codigo}/confirmacao")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void confirmar(@PathVariable String codigo) {
		service.confirmar(codigo);
	}
	
	@CheckSecurity.Pedidos.PodeGerenciarPedidos
	@PutMapping("/{codigo}/cancelamento")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelar(@PathVariable String codigo) {
		service.cancelar(codigo);
	}

	@CheckSecurity.Pedidos.PodeGerenciarPedidos
	@PutMapping("/{codigo}/entrega")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void entregar(@PathVariable String codigo) {
		service.entregar(codigo);
	}
	
	private Pageable traduzirPageable(Pageable pageable) {
		var mapeamento = Map.of(
				"codigo", "codigo",
				"subtotal", "subtotal",
				"taxaFrete", "taxaFrete",
				"valorTotal", "valorTotal",
				"dataCriacao", "dataCriacao",
				"restaurante.id", "restaurante.id",
				"restaurante.nome", "restaurante.nome",
				"cliente.id", "cliente.id",
				"cliente.nome", "cliente.nome"
				);
		return PageableTranslator.translate(pageable, mapeamento);
	}
	
	
	
}