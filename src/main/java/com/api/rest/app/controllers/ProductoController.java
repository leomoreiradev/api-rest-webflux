package com.api.rest.app.controllers;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.api.rest.app.models.documents.Producto;

import com.api.rest.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
	
	@Autowired
	private ProductoService productoService;
	
	@Value("${config.uploads.path}")
	private String  path;
	
	
	//Criando um produto com o arquivo
	@PostMapping("/v2")
	public Mono<ResponseEntity<Producto>> crearConFoto (Producto producto, @RequestPart FilePart file){
		if(producto.getCreateAt() == null) {
			producto.setCreateAt(new Date());
		}
		
		producto.setFoto(UUID.randomUUID().toString() + "-"+ file.filename()
		.replace(" ","")
		.replace(":","")
		.replace("\\",""));
		//Guardando a foto no diretorio (file.transferTo(new File(path + producto.getFoto())) - depois salvando
		return file.transferTo(new File(path + producto.getFoto())).then(productoService.save(producto))
				        .map(p -> ResponseEntity
						.created(URI.create("/api/productos/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(p));
	}
	
	
	//Fazendo upçoad de arquivo
	@PostMapping("/upload/{id}")
	public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart FilePart file){
		return productoService.findById(id).flatMap(p -> {
			p.setFoto(UUID.randomUUID().toString() + "-"+ file.filename()
			.replace(" ","")
			.replace(":","")
			.replace("\\",""));
			
			return file.transferTo(new File(path + p.getFoto())).then(productoService.save(p));
		}).map(p -> ResponseEntity.ok(p))
		.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	
	//Usando o ResponseEntity para poder conseguir manipular mais a resposta
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> lista() {
		return Mono.just(
				ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(productoService.findAll())
				);
	}
	
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>> ver(@PathVariable String id) {
		return productoService.findById(id).map(p -> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
		
	}
	
	@PostMapping
	public Mono<ResponseEntity<Producto>> crear (@RequestBody Producto producto){
		if(producto.getCreateAt() == null) {
			producto.setCreateAt(new Date());
		}
		
		return productoService.save(producto)
				.map(p -> ResponseEntity
						.created(URI.create("/api/productos/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(p));
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Producto>> editar (@RequestBody Producto producto, @PathVariable String id) {
		return productoService.findById(id).flatMap(p -> {
			p.setNombre(producto.getNombre());
			p.setPrecio(producto.getPrecio());
			p.setCategoria(producto.getCategoria());
			return productoService.save(p);
		}).map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
		.defaultIfEmpty(ResponseEntity.notFound().build());	

		
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id) {
		return productoService.findById(id).flatMap(p -> {
			return productoService.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));	
	}
	

}
