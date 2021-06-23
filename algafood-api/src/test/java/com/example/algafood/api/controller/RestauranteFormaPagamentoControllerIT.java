package com.example.algafood.api.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.example.algafood.api.exceptionhandler.ProblemType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class RestauranteFormaPagamentoControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/restaurantes";
				
		flyway.migrate();
	}
		
	@Test
	void deveRetornarStatus200_QuandoConsultarFormasPagamentoDoRestaurante() {		

		RestAssured
				.given()
				.pathParam("restauranteId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{restauranteId}/formas-pagamento")
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}

	@Test
	void deveRetornarStatus404_QuandoConsultarFormasPagamentoDoRestaurante() {		

		RestAssured
				.given()
				.pathParam("restauranteId", 10)
				.accept(ContentType.JSON)
			.when()
				.get("/{restauranteId}/formas-pagamento")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
		
	}

	@Test
	void deveRetornarStatus201_QuandoAssociarFormasPagamento() {		

		RestAssured
				.given()
				.pathParam("restauranteId", 2)
				.pathParam("formaPagamentoId", 1)
				.accept(ContentType.JSON)
			.when()
				.put("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
			.then()
				.statusCode(HttpStatus.CREATED.value());
		
	}

	@Test
	void deveRetornarStatus204_QuandoDesassociarFormasPagamento() {		

		RestAssured
				.given()
				.pathParam("restauranteId", 2)
				.pathParam("formaPagamentoId", 3)
				.accept(ContentType.JSON)
			.when()
				.delete("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
		
	}

}
