package com.example.algafood.api.model.input;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoIdInput {

	@NotNull
	private Long Id;

}
