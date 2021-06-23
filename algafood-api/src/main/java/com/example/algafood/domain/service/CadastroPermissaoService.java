package com.example.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.algafood.domain.exception.EntidadeEmUsoException;
import com.example.algafood.domain.exception.PermissaoNaoEncontradaException;
import com.example.algafood.domain.model.Permissao;
import com.example.algafood.domain.repository.PermissaoRepository;

@Service
public class CadastroPermissaoService {

	private static final String MSG_PERMISSAO_EM_USO = "Permissão de código %d não pode ser removida pois já está sendo utilizada";

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Transactional
	public Permissao salvar(Permissao permissao) {
		return permissaoRepository.save(permissao);
	}

	@Transactional
	public void excluir(Long id) {
		try {
			permissaoRepository.deleteById(id);
			permissaoRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new PermissaoNaoEncontradaException(id);

		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(MSG_PERMISSAO_EM_USO, id));
		}
	}
	
	public Permissao buscarOuFalhar(Long id) {
		return permissaoRepository.findById(id)
				.orElseThrow(() -> new PermissaoNaoEncontradaException(id));
	}

}
