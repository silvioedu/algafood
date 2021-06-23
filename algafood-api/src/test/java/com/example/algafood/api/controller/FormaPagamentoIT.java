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
class FormaPagamentoIT {

	private static final String JSON_PATH = "/json/formapagamento";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarFormaPagamentoJson;
	private String adicionarFormaPagamentoSemNomeJson;
	private String adicionarFormaPagamentoIncompreensivelJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/formas-pagamento";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarFormaPagamentoJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-forma-pagamento.json");
		adicionarFormaPagamentoSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-forma-pagamento-sem-nome.json");
		adicionarFormaPagamentoIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-forma-pagamento-incompreensivel.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarFormasPagamento() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarFormaPagamentoExistente() {
		
		RestAssured
			.given()
				.pathParam("formaPagamentoId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{formaPagamentoId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(1))
				.body("descricao", equalTo("Cartão de crédito"));
	}
	

	@Test
	void deveRetornarStatus409_QuandoRemoverFormaPagamentoEmUso() {
		RestAssured
			.given()
				.pathParam("formaPagamentoId", 2)
				.accept(ContentType.JSON)
			.when()
				.delete("/{formaPagamentoId}")
			.then()
				.statusCode(HttpStatus.CONFLICT.value())
				.body("status", equalTo(HttpStatus.CONFLICT.value()))
				.body("type", containsString(ProblemType.ENTIDADE_EM_USO.getUri()))
				.body("title", equalTo(ProblemType.ENTIDADE_EM_USO.getTitle()));
	}	

	@Test
	void deveRetornarStatus404_QuandoConsultarFormaPagamentoInexistente() {
		RestAssured
			.given()
				.pathParam("formaPagamentoId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{formaPagamentoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverFormaPagamentoExistente() {
		
		RestAssured
			.given()
				.pathParam("formaPagamentoId", 4)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{formaPagamentoId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverFormaPagamentoInexistente() {
		
		RestAssured
			.given()
				.pathParam("formaPagamentoId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{formaPagamentoId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarFormaPagamento() {
		
		RestAssured
			.given()
				.body(adicionarFormaPagamentoJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", equalTo(5));
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarFormaPagamentoSemNome() {
		
		RestAssured
			.given()
				.body(adicionarFormaPagamentoSemNomeJson)
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
	void deveRetornarStatu400_QuandoCadastrarFormaPagamentoIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarFormaPagamentoIncompreensivelJson)
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
