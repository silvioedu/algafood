package com.example.algafood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class ItemPedido {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal precoUnitario;
    private BigDecimal precoTotal;
    private Integer quantidade;
    private String observacao;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Produto produto;
    
    public void calcularPrecoTotal() {
        BigDecimal precoUnitario = this.getPrecoUnitario();
        BigDecimal quantidade = new BigDecimal(this.getQuantidade());

        if (Objects.isNull(precoUnitario)) {
            precoUnitario = BigDecimal.ZERO;
        }

        if (Objects.isNull(quantidade)) {
            quantidade = BigDecimal.ZERO;
        }

        this.setPrecoTotal(precoUnitario.multiply(quantidade));
    }

}
