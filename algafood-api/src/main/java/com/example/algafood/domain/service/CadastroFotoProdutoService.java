package com.example.algafood.domain.service;

import java.io.InputStream;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.algafood.domain.exception.FotoProdutoNaoEncontradoException;
import com.example.algafood.domain.model.FotoProduto;
import com.example.algafood.domain.repository.ProdutoRepository;
import com.example.algafood.domain.service.IFotoStorageService.NovaFoto;

@Service
public class CadastroFotoProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private IFotoStorageService fotoStorageService;

	@Transactional
	public FotoProduto salvar(FotoProduto foto, InputStream dadosArquivo) {
		Optional<FotoProduto> fotoExistente = produtoRepository.findFotoById(foto.getRestauranteId(),
				foto.getProduto().getId());
		String nomeArquivoExistente = null;

		if (fotoExistente.isPresent()) {
			nomeArquivoExistente = fotoExistente.get().getNomeArquivo();
			produtoRepository.delete(fotoExistente.get());
		}

		foto.setNomeArquivo(fotoStorageService.gerarNomeArquivo(foto.getNomeArquivo()));
		foto = produtoRepository.save(foto);
		produtoRepository.flush();

		NovaFoto novaFoto = NovaFoto.builder().nomeArquivo(foto.getNomeArquivo()).inputStream(dadosArquivo).build();

		fotoStorageService.substituir(nomeArquivoExistente, novaFoto);

		return foto;
	}

	public FotoProduto buscarOuFalhar(Long restauranteId, Long produtoId) {
		return produtoRepository.findFotoById(restauranteId, produtoId)
				.orElseThrow(() -> new FotoProdutoNaoEncontradoException(restauranteId, produtoId));
	}

	@Transactional
	public void excluir(Long restauranteId, Long produtoId) {
		FotoProduto foto = buscarOuFalhar(restauranteId, produtoId);
		
		produtoRepository.delete(foto);
		produtoRepository.flush();
		
		fotoStorageService.remover(foto.getNomeArquivo());
	}

}
