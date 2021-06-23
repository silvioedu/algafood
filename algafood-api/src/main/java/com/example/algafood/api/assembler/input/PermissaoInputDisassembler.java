package com.example.algafood.api.assembler.input;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.input.PermissaoInput;
import com.example.algafood.domain.model.Permissao;

@Component
public class PermissaoInputDisassembler {

    @Autowired
    private ModelMapper modelMapper;
    
    public Permissao toDomain(PermissaoInput permissaoInput) {
        return modelMapper.map(permissaoInput, Permissao.class);
    }
    
    public void copyToOutput(PermissaoInput permissaoInput, Permissao permissao) {
        modelMapper.map(permissaoInput, permissao);
    }   
}                
