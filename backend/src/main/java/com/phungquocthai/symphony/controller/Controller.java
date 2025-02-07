package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface Controller<T> {
	@PostMapping
	public ResponseEntity<T> create(@RequestBody T dto);
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete();
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<T> update(@RequestBody T dto);
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<T> findById();
	
	@GetMapping
	public ResponseEntity<List<T>> findAll();
	
}
