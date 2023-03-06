package com.productdesc.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.productdesc.entities.Product;
import com.productdesc.entities.User;
import com.productdesc.repositories.ProductRepository;
import com.productdesc.repositories.UserRepository;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	
	// search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query, Principal principle){
		System.out.println(query);
		User user = this.userRepository.getUserByUserName(principle.getName());
		List<Product> products = this.productRepository.findByPnameContainingAndUser(query, user);
		
		return ResponseEntity.ok(products);		
	}
	
}
