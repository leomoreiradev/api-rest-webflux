package com.api.rest.app;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.api.rest.app.models.documents.Categoria;
import com.api.rest.app.models.documents.Producto;
import com.api.rest.app.models.services.ProductoService;

import reactor.core.publisher.Mono;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private ProductoService productoService;
	
	@Test
	void listarTest() {
		
		client.get()
		.uri("/api/productos")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Producto.class)
		.hasSize(10);
	}
	
	
	@Test
	void verTest() {
		Producto producto = productoService.findByNombre("TV Panasonic Pantalla LCD").block();
		client.get()
		.uri("/api/productos/{id}", Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith(response -> {
			Producto p = response.getResponseBody();
			Assertions.assertThat(p.getNombre()).isEqualTo("TV Panasonic Pantalla LCD");
		});
		
		// OU 
//		.expectBody()
//		.jsonPath("$.id").isNotEmpty()
//		.jsonPath("$.nombre").isEqualTo("TV Panasonic Pantalla LCD");
	}
	
	
	@Test
	public void crearTest() {
		//O block() retonar uma categoria e não um Mono<Categoria>
		Categoria categoria = productoService.findByCategoriaNombre("Muebles").block();
		
		Producto producto = new Producto("Mesa comedor", 100.00, categoria);
		
		client.post()
		.uri("/api/productos")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Mesa comedor")
		.jsonPath("$.categoria.nombre").isEqualTo("Muebles");
	}
	
	
	@Test
	public void editarTest() {
		//Continuar daqui aula 83
		Producto producto = productoService.finDByNombre()
	}
	
	
	

}
