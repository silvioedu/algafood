package com.example.algafood.domain.service;

import com.example.algafood.api.model.input.filter.VendaDiariaFilter;

public interface IVendaReportService {

	byte[] emitirVendasDiarias(VendaDiariaFilter filtro, String timeOffset);
	
}
