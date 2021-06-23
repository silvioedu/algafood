package com.example.algafood.api.assembler.input;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.input.ProdutoInput;
import com.example.algafood.domain.model.Produto;

@Component
public class ProdutoInputDisassembler {

    @Autowired
    private ModelMapper modelMapper;
    
    public Produto toDomain(ProdutoInput produtoInput) {
        return modelMapper.map(produtoInput, Produto.class);
    }
    
    public void copyToOutput(ProdutoInput produtoInput, Produto produto) {
        modelMapper.map(produtoInput, produto);
    }   
}                
