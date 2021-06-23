package com.example.algafood.api.assembler.output;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.FotoProdutoModel;
import com.example.algafood.domain.model.FotoProduto;

@Component
public class FotoProdutoOutputAssembler {

    @Autowired
    private ModelMapper modelMapper;
    
    public FotoProdutoModel toDTO(FotoProduto fotoProduto) {
        return modelMapper.map(fotoProduto, FotoProdutoModel.class);
    }
    
}
