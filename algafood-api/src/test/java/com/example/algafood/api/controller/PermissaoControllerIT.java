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

import com.algaworks.algafood.util.ResourceUtils;
import com.example.algafood.api.exceptionhandler.ProblemType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class PermissaoControllerIT {

	private static final String JSON_PATH = "/json/permissao";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarPermissaoJson;
	private String adicionarPermissaoSemNomeJson;
	private String adicionarPermissaoIncompreensivelJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/permissoes";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarPermissaoJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-permissao.json");
		adicionarPermissaoSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-permissao-sem-nome.json");
		adicionarPermissaoIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-permissao-incompreensivel.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarPermissoes() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarPermissaoExistente() {
		
		RestAssured
			.given()
				.pathParam("permissaoId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{permissaoId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(1))
				.body("nome", equalTo("CONSULTAR_COZINHAS"));
	}
	
	@Test
	void deveRetornarStatus404_QuandoConsultarPermissaoInexistente() {
		RestAssured
			.given()
				.pathParam("permissaoId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{permissaoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverPermissaoExistente() {
		
		RestAssured
			.given()
				.pathParam("permissaoId", 3)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{permissaoId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverPermissaoInexistente() {
		
		RestAssured
			.given()
				.pathParam("permissaoId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{permissaoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoPermissaoCozinha() {
		
		RestAssured
			.given()
				.body(adicionarPermissaoJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", equalTo(4));
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarPermissaoSemNome() {
		
		RestAssured
			.given()
				.body(adicionarPermissaoSemNomeJson)
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
	void deveRetornarStatu400_QuandoCadastrarPermissaoIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarPermissaoIncompreensivelJson)
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
