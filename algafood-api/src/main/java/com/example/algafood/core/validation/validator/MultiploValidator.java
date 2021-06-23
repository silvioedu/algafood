package com.example.algafood.core.validation.validator;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.algafood.core.validation.Multiplo;

public class MultiploValidator implements ConstraintValidator<Multiplo, Number>{

	private int numeroMultiplo;
	
	@Override
	public void initialize(Multiplo constraintAnnotation) {
		this.numeroMultiplo = constraintAnnotation.numero();
	}
	
	@Override
	public boolean isValid(Number value, ConstraintValidatorContext context) {
		boolean valido = true;
		
		if (Objects.nonNull(value)) { 
			var valorDecimal = BigDecimal.valueOf(value.doubleValue());
			var multiploDecimal = BigDecimal.valueOf(this.numeroMultiplo);
			var resto = valorDecimal.remainder(multiploDecimal);

			valido = BigDecimal.ZERO.compareTo(resto) == 0;
		}
		
		return valido;
	}

	
	
}
