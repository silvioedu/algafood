package com.example.algafood.api.model.input;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissaoInput {

	@NotBlank
	private String nome;
	private String descricao;

}
