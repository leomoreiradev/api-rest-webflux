package com.api.rest.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.api.rest.app.models.documents.Categoria;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String>{

}
