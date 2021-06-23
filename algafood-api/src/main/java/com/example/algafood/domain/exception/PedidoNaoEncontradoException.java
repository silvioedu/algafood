package com.example.algafood.domain.exception;

public class PedidoNaoEncontradoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de pedido com código %s";

	public PedidoNaoEncontradoException(String codigo) {
		super(String.format(MSG_NAO_ENCONTRADO, codigo));
	}

}
