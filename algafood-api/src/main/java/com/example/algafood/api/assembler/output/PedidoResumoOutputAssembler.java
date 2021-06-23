package com.example.algafood.api.assembler.output;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.PedidoResumoModel;
import com.example.algafood.domain.model.Pedido;

@Component
public class PedidoResumoOutputAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public PedidoResumoModel toDTO(Pedido produto) {
		return modelMapper.map(produto, PedidoResumoModel.class);
	}
	
	public List<PedidoResumoModel> toCollectionDTO(List<Pedido> produtos) {
		return produtos.stream()
				.map(pedido -> toDTO(pedido))
				.collect(Collectors.toList());
	}
}	 

