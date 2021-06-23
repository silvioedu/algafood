package com.example.algafood.api.assembler.output;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.ProdutoModel;
import com.example.algafood.domain.model.Produto;

@Component
public class ProdutoOutputAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public ProdutoModel toDTO(Produto produto) {
		return modelMapper.map(produto, ProdutoModel.class);
	}
	
	public List<ProdutoModel> toCollectionDTO(List<Produto> produtos) {
		return produtos.stream()
				.map(produto -> toDTO(produto))
				.collect(Collectors.toList());
	}
}	 

