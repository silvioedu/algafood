package com.example.algafood.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.algafood.api.model.VendaDiaria;
import com.example.algafood.api.model.input.filter.VendaDiariaFilter;
import com.example.algafood.core.security.CheckSecurity;
import com.example.algafood.domain.service.IVendaQueryService;
import com.example.algafood.domain.service.IVendaReportService;

@RestController
@RequestMapping("/estatisticas")
public class EstatisticasController {

	@Autowired
	private IVendaQueryService vendaQueryService;
	
	@Autowired
	private IVendaReportService vendaReportService;
	
	@CheckSecurity.Estatisticas.PodeConsultar
	@GetMapping(path = "/vendas-diarias", produces = MediaType.APPLICATION_JSON_VALUE) 
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter filtro, 
			@RequestParam(required=false, defaultValue = "+00:00") String timeOffSet){
		return vendaQueryService.consultarVendasDiarias(filtro, timeOffSet);
	}
	
	@CheckSecurity.Estatisticas.PodeConsultar
	@GetMapping(path = "/vendas-diarias", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> consultarVendasDiariasPDF(VendaDiariaFilter filtro, 
			@RequestParam(required=false, defaultValue = "+00:00") String timeOffSet){
		
		byte[] bytesPDF = vendaReportService.emitirVendasDiarias(filtro, timeOffSet);
		
		var headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vendas-diarias.pdf");
		
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.headers(headers)
				.body(bytesPDF);
	}

}
