package com.example.algafood.api.assembler.output;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.GrupoModel;
import com.example.algafood.domain.model.Grupo;

@Component
public class GrupoOutputAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public GrupoModel toDTO(Grupo grupo) {
		return modelMapper.map(grupo, GrupoModel.class);
	}
	
	public List<GrupoModel> toCollectionDTO(Collection<Grupo> grupos) {
		return grupos.stream()
				.map(grupo -> toDTO(grupo))
				.collect(Collectors.toList());
	}
}	 

