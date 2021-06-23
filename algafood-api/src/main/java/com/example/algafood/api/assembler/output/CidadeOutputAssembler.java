package com.example.algafood.api.assembler.output;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.algafood.api.model.CidadeModel;
import com.example.algafood.domain.model.Cidade;

@Component
public class CidadeOutputAssembler {

    @Autowired
    private ModelMapper modelMapper;
    
    public CidadeModel toDTO(Cidade cidade) {
        return modelMapper.map(cidade, CidadeModel.class);
    }
    
    public List<CidadeModel> toCollectionDTO(List<Cidade> cidades) {
        return cidades.stream()
                .map(cidade -> toDTO(cidade))
                .collect(Collectors.toList());
    }

}
