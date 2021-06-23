package com.example.algafood.domain.service;

import java.util.List;

import com.example.algafood.api.model.VendaDiaria;
import com.example.algafood.api.model.input.filter.VendaDiariaFilter;

public interface IVendaQueryService {

	List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter vendaDiariaFilter, String timeOffSet);
}
