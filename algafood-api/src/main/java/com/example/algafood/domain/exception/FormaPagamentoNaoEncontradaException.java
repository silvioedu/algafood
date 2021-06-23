package com.example.algafood.domain.exception;

public class FormaPagamentoNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de forma de pagamento com código %d";

	public FormaPagamentoNaoEncontradaException(String mensagem) {
		super(mensagem);
	}
	
	public FormaPagamentoNaoEncontradaException(Long id) {
		this(String.format(MSG_NAO_ENCONTRADO, id));
	}

}
