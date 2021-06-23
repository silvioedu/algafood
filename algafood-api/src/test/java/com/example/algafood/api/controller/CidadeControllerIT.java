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
class CidadeControllerIT {

	private static final String JSON_PATH = "/json/cidade";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarCidadeJson;
	private String adicionarCidadeSemNomeJson;
	private String adicionarCidadeIncompreensivelJson;
	private String adicionarCidadeEstadoInexistenteJson;
	private String adicionarCidadeSemEstadoJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cidades";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarCidadeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cidade.json");
		adicionarCidadeSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cidade-sem-nome.json");
		adicionarCidadeIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cidade-incompreensivel.json");
		adicionarCidadeEstadoInexistenteJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cidade-estado-inexistente.json");
		adicionarCidadeSemEstadoJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-cidade-sem-estado.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarCidades() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	@Test
	void deveRetornarRespostaEStatusCorretos_QuantoConsultarCidadeExistente() {
		
		RestAssured
			.given()
				.pathParam("cidadeId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{cidadeId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(1))
				.body("nome", equalTo("Uberlândia"))
				.body("estado.id", equalTo(1));
		
	}
	
	@Test
	void deveRetornarStatus404_QuandoConsultarCidadeInexistente() {
		RestAssured
			.given()
				.pathParam("cidadeId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{cidadeId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	
	
	@Test
	void deveRetornarStatus409_QuandoRemoverCidadeEmUso() {
		RestAssured
			.given()
				.pathParam("cidadeId", 1)
				.accept(ContentType.JSON)
			.when()
				.delete("/{cidadeId}")
			.then()
				.statusCode(HttpStatus.CONFLICT.value())
				.body("status", equalTo(HttpStatus.CONFLICT.value()))
				.body("type", containsString(ProblemType.ENTIDADE_EM_USO.getUri()))
				.body("title", equalTo(ProblemType.ENTIDADE_EM_USO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverCidadeExistente() {
		
		RestAssured
			.given()
				.pathParam("cidadeId", 5)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{cidadeId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverCidadeInexistente() {
		
		RestAssured
			.given()
				.pathParam("cidadeId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{cidadeId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarCidade() {
		
		RestAssured
			.given()
				.body(adicionarCidadeJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", equalTo(6));
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarCidadeSemNome() {
		
		RestAssured
			.given()
				.body(adicionarCidadeSemNomeJson)
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
	void deveRetornarStatu400_QuandoCadastrarCidadeIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarCidadeIncompreensivelJson)
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

	@Test
	void deveRetornarStatu400_QuandoCadastrarCidadeComEstadoInexistente() {
		
		RestAssured
			.given()
				.body(adicionarCidadeEstadoInexistenteJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
				.body("type", containsString(ProblemType.ERRO_DE_NEGOCIO.getUri()))
				.body("title", equalTo(ProblemType.ERRO_DE_NEGOCIO.getTitle()));

	}

	@Test
	void deveRetornarStatu400_QuandoCadastrarCidadeSemEstado() {
		
		RestAssured
			.given()
				.body(adicionarCidadeSemEstadoJson)
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
	
}
