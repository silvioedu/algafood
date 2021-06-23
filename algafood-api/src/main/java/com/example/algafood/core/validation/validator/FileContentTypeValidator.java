package com.example.algafood.core.validation.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import com.example.algafood.core.validation.FileContentType;

public class FileContentTypeValidator implements ConstraintValidator<FileContentType, MultipartFile>{

	private List<String> allowedContentTypes;
	
	@Override
	public void initialize(FileContentType constraintAnnotation) {
		this.allowedContentTypes = Arrays.asList(constraintAnnotation.allowed());
	}
	
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {		
		return Objects.isNull(value) || this.allowedContentTypes.contains(value.getContentType());
	}

}
