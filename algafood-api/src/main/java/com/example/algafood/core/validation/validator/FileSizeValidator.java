package com.example.algafood.core.validation.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.example.algafood.core.validation.FileSize;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile>{

	private DataSize maxSize;
	
	@Override
	public void initialize(FileSize constraintAnnotation) {
		this.maxSize = DataSize.parse(constraintAnnotation.max());
	}
	
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {		
		return Objects.isNull(value) || value.getSize() <= this.maxSize.toBytes();
	}

}
