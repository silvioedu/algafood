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
class GrupoControllerIT {

	private static final String JSON_PATH = "/json/grupo";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarGrupoJson;
	private String adicionarGrupoSemNomeJson;
	private String adicionarGrupoIncompreensivelJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/grupos";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarGrupoJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-grupo.json");
		adicionarGrupoSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-grupo-sem-nome.json");
		adicionarGrupoIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-grupo-incompreensivel.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarGrupos() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	
	@Test
	void deveConterGruposEspecificas_QuandoConsultarGrupos() {

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("nome", Matchers.hasItems("Secretária", "Vendedor"));
	}
		
	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarGrupoExistente() {
		
		RestAssured
			.given()
				.pathParam("grupoId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{grupoId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(1))
				.body("nome", equalTo("Gerente"));
	}
	

	/*
	 * @Test void deveRetornarStatus409_QuandoRemoverGrupoEmUso() { RestAssured
	 * .given() .pathParam("estadoId", 3) .accept(ContentType.JSON) .when()
	 * .delete("/{estadoId}") .then() .statusCode(HttpStatus.CONFLICT.value())
	 * .body("status", equalTo(HttpStatus.CONFLICT.value())) .body("type",
	 * containsString(ProblemType.ENTIDADE_EM_USO.getUri())) .body("title",
	 * equalTo(ProblemType.ENTIDADE_EM_USO.getTitle())); }
	 */
	
	@Test
	void deveRetornarStatus404_QuandoConsultarGrupoInexistente() {
		RestAssured
			.given()
				.pathParam("grupoId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{grupoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverGrupoExistente() {
		
		RestAssured
			.given()
				.pathParam("grupoId", 1)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{grupoId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverGrupoInexistente() {
		
		RestAssured
			.given()
				.pathParam("grupoId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{grupoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarGrupo() {
		
		RestAssured
			.given()
				.body(adicionarGrupoJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", equalTo(5));
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarGrupoSemNome() {
		
		RestAssured
			.given()
				.body(adicionarGrupoSemNomeJson)
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
	void deveRetornarStatu400_QuandoCadastrarGrupoIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarGrupoIncompreensivelJson)
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
