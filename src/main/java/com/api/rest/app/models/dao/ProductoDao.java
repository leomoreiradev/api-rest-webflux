package com.api.rest.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.api.rest.app.models.documents.Producto;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String>{

}
