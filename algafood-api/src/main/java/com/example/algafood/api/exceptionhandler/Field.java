package com.example.algafood.api.exceptionhandler;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Field {
	
	private String name;
	private String userMessage;

}
