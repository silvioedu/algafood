package com.example.algafood.api.assembler.output;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.FormaPagamentoModel;
import com.example.algafood.domain.model.FormaPagamento;

@Component
public class FormaPagamentoOutputAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public FormaPagamentoModel toDTO(FormaPagamento formaPagamento) {
		return modelMapper.map(formaPagamento, FormaPagamentoModel.class);
	}
	
	public List<FormaPagamentoModel> toCollectionDTO(Collection<FormaPagamento> formaspagamento) {
		return formaspagamento.stream()
				.map(formaPagamento -> toDTO(formaPagamento))
				.collect(Collectors.toList());
	}
}	 

