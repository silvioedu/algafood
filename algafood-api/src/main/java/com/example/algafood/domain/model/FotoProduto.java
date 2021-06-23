package com.example.algafood.domain.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
public class FotoProduto {

	@EqualsAndHashCode.Include
	@Id
	@Column(name = "produto_id")
	private Long id;
	
	private String nomeArquivo;
	private String descricao;
	private String contentType;
	private Long tamanho;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	private Produto produto;

	public Long getRestauranteId() {
		if (Objects.nonNull(getProduto())) {
			return getProduto().getRestaurante().getId();
		}
		return null;
	}
}