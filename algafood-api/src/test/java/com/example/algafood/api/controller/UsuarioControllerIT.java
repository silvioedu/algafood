package com.example.algafood.api.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

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
class UsuarioControllerIT {

	private static final String JSON_PATH = "/json/usuario";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarUsuarioJson;
	private String adicionarUsuarioSemNomeJson;
	private String adicionarUsuarioIncompreensivelJson;
	private String atualizarUsuarioSenhaIncorretaJson;
	private String atualizarUsuarioSenhaCorretaJson;
	private String atualizarUsuarioEmailJaExisteJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/usuarios";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarUsuarioJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-usuario.json");
		adicionarUsuarioSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-usuario-sem-nome.json");
		adicionarUsuarioIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-usuario-incompreensivel.json");
		atualizarUsuarioSenhaIncorretaJson = ResourceUtils.getContentFromResource(JSON_PATH + "/atualizar-senha-usuario-senha-incorreta.json");
		atualizarUsuarioSenhaCorretaJson = ResourceUtils.getContentFromResource(JSON_PATH + "/atualizar-senha-usuario-senha-correta.json");
		atualizarUsuarioEmailJaExisteJson = ResourceUtils.getContentFromResource(JSON_PATH + "/atualizar-usuario-email-ja-existe.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarUsuarios() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	
	@Test
	void deveConterUsuariosEspecificos_QuandoConsultarUsuarios() {

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("nome", Matchers.hasItems("Maria Joaquina", "José Souza"));
	}
		
	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarUsuarioExistente() {
		
		RestAssured
			.given()
				.pathParam("usuarioId", 2)
				.accept(ContentType.JSON)
			.when()
				.get("/{usuarioId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(2))
				.body("nome", equalTo("Maria Joaquina"))
				.body("email", equalTo("maria.vnd@algafood.com"));
	}
	

	@Test
	void deveRetornarStatus404_QuandoConsultarUsuarioInexistente() {
		RestAssured
			.given()
				.pathParam("usuarioId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{usuarioId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverUsuarioExistente() {
		
		RestAssured
			.given()
				.pathParam("usuarioId", 4)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{usuarioId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverUsuarioInexistente() {
		
		RestAssured
			.given()
				.pathParam("usuarioId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{usuarioId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarUsuario() {
		
		RestAssured
			.given()
				.body(adicionarUsuarioJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", notNullValue());
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarUsuarioSemNome() {
		
		RestAssured
			.given()
				.body(adicionarUsuarioSemNomeJson)
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
	void deveRetornarStatu400_QuandoCadastrarUsuarioIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarUsuarioIncompreensivelJson)
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
	void deveRetornarStatus400_QuandoAtualizarSenhaComAtualInvalida() {
		
		RestAssured
			.given()
				.pathParam("usuarioId", 2)
				.body(atualizarUsuarioSenhaIncorretaJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.put("/{usuarioId}/senha")
			.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
				.body("type", containsString(ProblemType.ERRO_DE_NEGOCIO.getUri()))
				.body("title", equalTo(ProblemType.ERRO_DE_NEGOCIO.getTitle()));

	}

	@Test
	void deveRetornarStatus204_QuandoAtualizarSenhaComAtualValida() {
		
		RestAssured
			.given()
				.pathParam("usuarioId", 2)
				.body(atualizarUsuarioSenhaCorretaJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.put("/{usuarioId}/senha")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());

	}

	@Test
	void deveRetornarStatus400_QuandoEmailJaExiste() {
		
		RestAssured
			.given()
				.pathParam("usuarioId", 2)
				.body(atualizarUsuarioEmailJaExisteJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.put("/{usuarioId}")
			.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
				.body("type", containsString(ProblemType.ERRO_DE_NEGOCIO.getUri()))
				.body("title", equalTo(ProblemType.ERRO_DE_NEGOCIO.getTitle()));

	}
	
}