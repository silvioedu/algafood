package com.example.algafood.infrastructure.service.storage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.example.algafood.domain.service.IFotoStorageService;
import com.example.algafood.infrastructure.service.storage.exception.StorageException;

@Service
public class LocalFotoStorageServiceImpl implements IFotoStorageService {

	private static final String MSG_PROBLEMAS_SALVAR = "Não foi possível armazenar o arquivo";
	private static final String MSG_PROBLEMAS_REMOVER = "Não foi possível excluir o arquivo";
	private static final String MSG_PROBLEMAS_RECUPERAR = "Não foi possível recuperar o arquivo";

	
	@Value("${algafood.storage.local.diretorio-fotos}")
	private Path diretorioFotos;
	
	@Override
	public void armazenar(NovaFoto novaFoto) {
		try {
			Path arquivoPath = getArquivoPath(novaFoto.getNomeArquivo());
		
			FileCopyUtils.copy(novaFoto.getInputStream(), Files.newOutputStream(arquivoPath));

		} catch (Exception e) {
			throw new StorageException(MSG_PROBLEMAS_SALVAR, e);
		}
		
	}
	
	private Path getArquivoPath(String nomeArquivo) {
		return diretorioFotos.resolve(Path.of(nomeArquivo));
	}

	@Override
	public void remover(String nomeArquivo) {
		try {
			Path arquivoPath = getArquivoPath(nomeArquivo);
			Files.deleteIfExists(arquivoPath);
		} catch (Exception e) {
			throw new StorageException(MSG_PROBLEMAS_REMOVER, e);
		}

	}

	@Override
	public InputStream recuperar(String nomeArquivo) {
		try {
			Path arquivoPath = getArquivoPath(nomeArquivo);
			
			return Files.newInputStream(arquivoPath);
		} catch (Exception e) {
			throw new StorageException(MSG_PROBLEMAS_RECUPERAR, e);
		}
	}

}
