package com.example.algafood.api.assembler.output;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.EstadoModel;
import com.example.algafood.domain.model.Estado;

@Component
public class EstadoOutputAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public EstadoModel toDTO(Estado estado) {
		return modelMapper.map(estado, EstadoModel.class);
	}
	
	public List<EstadoModel> toCollectionDTO(List<Estado> estados) {
		return estados.stream()
				.map(estado -> toDTO(estado))
				.collect(Collectors.toList());
	}
}	 

