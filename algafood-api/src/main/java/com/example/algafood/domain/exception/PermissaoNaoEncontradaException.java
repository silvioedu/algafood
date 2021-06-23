package com.example.algafood.domain.exception;

public class PermissaoNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de permissão com código %d";

	public PermissaoNaoEncontradaException(String mensagem) {
		super(mensagem);
	}
	
	public PermissaoNaoEncontradaException(Long id) {
		this(String.format(MSG_NAO_ENCONTRADO, id));
	}

}
