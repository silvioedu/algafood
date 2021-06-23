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
class RestauranteControllerIT {

	private static final String JSON_PATH = "/json/restaurante";

	@LocalServerPort
	private int port;
	
	@Autowired
	private Flyway flyway;
	
	private String adicionarRestauranteJson;
	private String adicionarRestauranteSemNomeJson;
	private String adicionarRestauranteTaxaFreteNegativaJson;
	private String adicionarRestauranteIncompreensivelJson;
	private String adicionarRestauranteCozinhaInexistenteJson;
	private String adicionarRestauranteSemCozinhaJson;
	
	@BeforeAll
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/restaurantes";
		
		carregaJson();
		
		flyway.migrate();
	}
	
	void carregaJson() {
		adicionarRestauranteJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-restaurante.json");
		adicionarRestauranteSemNomeJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-restaurante-sem-nome.json");
		adicionarRestauranteTaxaFreteNegativaJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-restaurante-taxa-frete-negativa.json");
		adicionarRestauranteIncompreensivelJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-restaurante-incompreensivel.json");
		adicionarRestauranteCozinhaInexistenteJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-restaurante-cozinha-inexistente.json");
		adicionarRestauranteSemCozinhaJson = ResourceUtils.getContentFromResource(JSON_PATH + "/adicionar-restaurante-sem-cozinha.json");
	}
	
	@Test
	void deveRetornarStatus200_QuandoConsultarRestaurantes() {		

		RestAssured
			.given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.statusCode(HttpStatus.OK.value());
		
	}
	
	@Test
	void deveRetornarRespostaEStatusCorretos_QuantoConsultarRestauranteExistente() {
		
		RestAssured
			.given()
				.pathParam("restauranteId", 1)
				.accept(ContentType.JSON)
			.when()
				.get("/{restauranteId}")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(1))
				.body("nome", equalTo("Thai Gourmet"))
				.body("taxaFrete", equalTo(10.0f))
				.body("cozinha.id", equalTo(1))
				.body("cozinha.nome", equalTo("Tailandesa"));
		
	}
	
	@Test
	void deveRetornarStatus404_QuandoConsultarRestauranteInexistente() {
		RestAssured
			.given()
				.pathParam("restauranteId", 100)
				.accept(ContentType.JSON)
			.when()
				.get("/{restauranteId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));
	}	
	
	@Test
	void deveRetornarStatus409_QuandoRemoverRestauranteEmUso() {
		RestAssured
			.given()
				.pathParam("restauranteId", 1)
				.accept(ContentType.JSON)
			.when()
				.delete("/{restauranteId}")
			.then()
				.statusCode(HttpStatus.CONFLICT.value())
				.body("status", equalTo(HttpStatus.CONFLICT.value()))
				.body("type", containsString(ProblemType.ENTIDADE_EM_USO.getUri()))
				.body("title", equalTo(ProblemType.ENTIDADE_EM_USO.getTitle()));
	}	

	@Test
	void deveRetornarStatus204_QuandoRemoverRestauranteExistente() {
		
		RestAssured
			.given()
				.pathParam("restauranteId", 7)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.delete("/{restauranteId}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoRemoverRestauranteInexistente() {
		
		RestAssured
			.given()
				.pathParam("restauranteId", 100)
				.accept(ContentType.JSON)
			.when()
				.delete("/{restauranteId}")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.body("status", equalTo(HttpStatus.NOT_FOUND.value()))
				.body("type", containsString(ProblemType.RECURSO_NAO_ENCONTRADO.getUri()))
				.body("title", equalTo(ProblemType.RECURSO_NAO_ENCONTRADO.getTitle()));

	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarRestaurante() {
		
		RestAssured
			.given()
				.body(adicionarRestauranteJson)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", equalTo(8));
	}
	
	@Test
	void deveRetornarStatu400_QuandoCadastrarRestauranteSemNome() {
		
		RestAssured
			.given()
				.body(adicionarRestauranteSemNomeJson)
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
	void deveRetornarStatu400_QuandoCadastrarRestauranteTaxaFreteNegativa() {
		
		RestAssured
			.given()
				.body(adicionarRestauranteTaxaFreteNegativaJson)
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
	void deveRetornarStatu400_QuandoCadastrarRestauranteIncompreensivel() {
		
		RestAssured
			.given()
				.body(adicionarRestauranteIncompreensivelJson)
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
	void deveRetornarStatu400_QuandoCadastrarRestauranteComCozinhaInexistente() {
		
		RestAssured
			.given()
				.body(adicionarRestauranteCozinhaInexistenteJson)
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
	void deveRetornarStatu400_QuandoCadastrarRestauranteSemCozinha() {
		
		RestAssured
			.given()
				.body(adicionarRestauranteSemCozinhaJson)
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
	void deveRetornarStatus204_QuandoAtivarRestaurante() {
		RestAssured
			.given()
				.pathParam("restauranteId", 1)
				.accept(ContentType.JSON)
			.when()
				.put("/{restauranteId}/ativo")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	void deveRetornarStatus204_QuandoInativarRestaurante() {
		RestAssured
			.given()
				.pathParam("restauranteId", 1)
				.accept(ContentType.JSON)
			.when()
				.put("/{restauranteId}/ativo")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

}
