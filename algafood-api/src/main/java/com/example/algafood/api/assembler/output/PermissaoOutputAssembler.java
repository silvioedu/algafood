package com.example.algafood.api.assembler.output;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.PermissaoModel;
import com.example.algafood.domain.model.Permissao;

@Component
public class PermissaoOutputAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public PermissaoModel toDTO(Permissao permissao) {
		return modelMapper.map(permissao, PermissaoModel.class);
	}
	
	public List<PermissaoModel> toCollectionDTO(Collection<Permissao> permissoes) {
		return permissoes.stream()
				.map(permissao -> toDTO(permissao))
				.collect(Collectors.toList());
	}
}	 

