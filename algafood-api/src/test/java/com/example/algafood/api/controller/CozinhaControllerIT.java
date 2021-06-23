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
class CozinhaControllerIT {

	private static final String JSON_PATH = "/json/cozinha";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarCozinhaAlemaJson;
	private String adicionarCozinhaSemNomeJson;
	private String adicionarCozinhaIncompreensivelJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarCozinhaAlemaJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cozinha-alema.json");
		adicionarCozinhaSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cozinha-sem-nome.json");
		adicionarCozinhaIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cozinha-incompreensivel.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarCozinhas() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	@Test
	void deveConterCincoCozinhas_QuandoConsultarCozinhas() {

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("", Matchers.hasSize(5));
	}
	
	@Test
	void deveConterCozinhasEspecificas_QuandoConsultarCozinhas() {

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("nome", Matchers.hasItems("Indiana", "Tailandesa"));
	}
		
	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		
		RestAssured
			.given()
				.pathParam("cozinhaId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{cozinhaId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(1))
				.body("nome", equalTo("Tailandesa"));
	}
	
	@Test
	void deveRetornarStatus404_QuandoConsultarCozinhaInexistente() {
		RestAssured
			.given()
				.pathParam("cozinhaId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{cozinhaId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverCozinhaExistente() {
		
		RestAssured
			.given()
				.pathParam("cozinhaId", 5)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{cozinhaId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverCozinhaInexistente() {
		
		RestAssured
			.given()
				.pathParam("cozinhaId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{cozinhaId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarCozinha() {
		
		RestAssured
			.given()
				.body(adicionarCozinhaAlemaJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", equalTo(6));
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarCozinhaSemNome() {
		
		RestAssured
			.given()
				.body(adicionarCozinhaSemNomeJson)
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
	void deveRetornarStatu400_QuandoCadastrarCozinhaIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarCozinhaIncompreensivelJson)
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
