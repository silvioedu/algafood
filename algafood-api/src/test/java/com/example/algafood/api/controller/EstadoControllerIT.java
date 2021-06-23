package com.example.algafood.api.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.algaworks.algafood.util.ResourceUtils;
import com.example.algafood.api.exceptionhandler.ProblemType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class EstadoControllerIT {

	private static final String JSON_PATH = "/json/estado";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarEstadoJson;
	private String adicionarEstadoSemNomeJson;
	private String adicionarEstadoIncompreensivelJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/estados";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarEstadoJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-estado.json");
		adicionarEstadoSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-estado-sem-nome.json");
		adicionarEstadoIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-estado-incompreensivel.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarEstados() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	
	@Test
	void deveConterEstadosEspecificas_QuandoConsultarEstados() {

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("nome", Matchers.hasItems("Minas Gerais", "São Paulo"));
	}
		
	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarEstadoExistente() {
		
		RestAssured
			.given()
				.pathParam("estadoId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{estadoId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(1))
				.body("nome", equalTo("Minas Gerais"));
	}
	

	@Test
	void deveRetornarStatus409_QuandoRemoverEstadoEmUso() {
		RestAssured
			.given()
				.pathParam("estadoId", 3)
				.accept(ContentType.JSON)
			.when()
				.delete("/{estadoId}")
			.then()
				.statusCode(HttpStatus.CONFLICT.value())
				.body("status", equalTo(HttpStatus.CONFLICT.value()))
				.body("type", containsString(ProblemType.ENTIDADE_EM_USO.getUri()))
				.body("title", equalTo(ProblemType.ENTIDADE_EM_USO.getTitle()));
	}	

	@Test
	void deveRetornarStatus404_QuandoConsultarEstadoInexistente() {
		RestAssured
			.given()
				.pathParam("estadoId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{estadoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverEstadoExistente() {
		
		RestAssured
			.given()
				.pathParam("estadoId", 4)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{estadoId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverEstadoInexistente() {
		
		RestAssured
			.given()
				.pathParam("estadoId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{estadoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarEstado() {
		
		RestAssured
			.given()
				.body(adicionarEstadoJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", equalTo(5));
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarEstadoSemNome() {
		
		RestAssured
			.given()
				.body(adicionarEstadoSemNomeJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
				.body("type", containsString(ProblemType.DADOS_INVALIDOS.getUri()))
				.body("title", equalTo(ProblemType.DADOS_INVALIDOS.getTitle()));

	}

	@Test
	void deveRetornarStatu400_QuandoCadastrarEstadoIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarEstadoIncompreensivelJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
				.body("type", containsString(ProblemType.MENSAGEM_INCOMPREENSIVEL.getUri()))
				.body("title", equalTo(ProblemType.MENSAGEM_INCOMPREENSIVEL.getTitle()));

	}

}
