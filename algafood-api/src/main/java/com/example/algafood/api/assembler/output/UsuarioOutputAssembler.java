package com.example.algafood.api.assembler.output;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.UsuarioModel;
import com.example.algafood.domain.model.Usuario;

@Component
public class UsuarioOutputAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public UsuarioModel toDTO(Usuario usuario) {
		return modelMapper.map(usuario, UsuarioModel.class);
	}
	
	public List<UsuarioModel> toCollectionDTO(Collection<Usuario> usuarios) {
		return usuarios.stream()
				.map(usuario -> toDTO(usuario))
				.collect(Collectors.toList());
	}
}	 

