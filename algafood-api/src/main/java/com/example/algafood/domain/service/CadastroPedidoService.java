package com.example.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.algafood.domain.exception.NegocioException;
import com.example.algafood.domain.exception.PedidoNaoEncontradoException;
import com.example.algafood.domain.model.Cidade;
import com.example.algafood.domain.model.FormaPagamento;
import com.example.algafood.domain.model.Pedido;
import com.example.algafood.domain.model.Produto;
import com.example.algafood.domain.model.Restaurante;
import com.example.algafood.domain.model.Usuario;
import com.example.algafood.domain.repository.PedidoRepository;

@Service
public class CadastroPedidoService {

	private static final String MSG_FORMA_PAGAMENTO_NAO_ACEITA = "Forma de pagamento '%s' não é aceita por esse restaurante.";

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private CadastroCidadeService cidadeService;

	@Autowired
	private CadastroUsuarioService usuarioService;

	@Autowired
	private CadastroFormaPagamentoService formaPagamentoService;

	@Autowired
	private CadastroRestauranteService restauranteService;

	@Autowired
	private CadastroProdutoService produtoService;
	
	public Pedido buscarOuFalhar(String codigo) {
		return pedidoRepository.findByCodigo(codigo).orElseThrow(() -> new PedidoNaoEncontradoException(codigo));
	}

	@Transactional
	public Pedido emitir(Pedido pedido) {
		validarPedido(pedido);

		pedido.setTaxaFrete(pedido.getRestaurante().getTaxaFrete());
		pedido.calcularValorTotal();

		return pedidoRepository.save(pedido);
	}

	private void validarPedido(Pedido pedido) {
		Cidade cidade = cidadeService.buscarOuFalhar(pedido.getEnderecoEntrega().getCidade().getId());
		Usuario cliente = usuarioService.buscarOuFalhar(pedido.getCliente().getId());
		FormaPagamento formaPagamento = formaPagamentoService.buscarOuFalhar(pedido.getFormaPagamento().getId());
		Restaurante restaurante = restauranteService.buscarOuFalhar(pedido.getRestaurante().getId());

		pedido.setCliente(cliente);
		pedido.getEnderecoEntrega().setCidade(cidade);
		pedido.setRestaurante(restaurante);

		if (restaurante.naoAceitaFormaPagamento(formaPagamento)) {
			throw new NegocioException(String.format(MSG_FORMA_PAGAMENTO_NAO_ACEITA, formaPagamento.getDescricao()));
		}
		pedido.setFormaPagamento(formaPagamento);

		validarItens(pedido);
	}

	private void validarItens(Pedido pedido) {
		pedido.getItens().forEach(item -> {
			Produto produto = produtoService.buscarOuFalhar(pedido.getRestaurante().getId(), item.getProduto().getId());

			item.setPedido(pedido);
			item.setProduto(produto);
			item.setPrecoUnitario(produto.getPreco());
		});

	}

	@Transactional
	public void confirmar(String codigo) {
		Pedido pedido = this.buscarOuFalhar(codigo);
		pedido.confirmar();
		pedidoRepository.save(pedido);
	}
	
	@Transactional
	public void cancelar(String codigo) {
		Pedido pedido = this.buscarOuFalhar(codigo);
		pedido.cancelar();
		pedidoRepository.save(pedido);
	}

	@Transactional
	public void entregar(String codigo) {
		Pedido pedido = this.buscarOuFalhar(codigo);
		pedido.entregar();
	}
	
}
