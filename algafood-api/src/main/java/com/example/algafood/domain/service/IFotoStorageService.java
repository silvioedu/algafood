package com.example.algafood.domain.service;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

public interface IFotoStorageService {

	InputStream recuperar(String nomeArquivo);
	
	void armazenar(NovaFoto novaFoto);
	
	void remover(String nomeArquivo);

	default String gerarNomeArquivo(String nomeOriginal) {
		return UUID.randomUUID().toString() + "_" + nomeOriginal;
	}

	default void substituir(String nomeArquivoAntigo, NovaFoto novaFoto) {
		this.armazenar(novaFoto);
		
		if (Objects.nonNull(nomeArquivoAntigo)) {
			this.remover(nomeArquivoAntigo);
		}
	}
	@Builder
	@Getter
	class NovaFoto {

		private String nomeArquivo;
		private InputStream inputStream;

	}
}
