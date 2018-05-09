package de.hska.iwi.vslab.catalogmanagement.catalogmanagementservice;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@EnableCircuitBreaker
public class CatalogManagementController {

	@Autowired
    private ProductClient productClient;
    @Autowired  
    private CategoryClient categoryClient;

	/*
	------------PRODUCT-------------------
	*/
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Product>> getProducts() {
		return new ResponseEntity<Iterable<Product>>(productClient.getProducts(), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
	public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
		return new ResponseEntity<>(productClient.getProduct(productId), HttpStatus.OK);
	}
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public ResponseEntity<Product> createProduct(@RequestBody Product payload) {
		return new ResponseEntity<Product>(productClient.createProduct(payload), HttpStatus.OK);
	}
	@RequestMapping(value = "/products/{productId}", method = RequestMethod.PUT)
	public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product payload) {
		return new ResponseEntity<>(productClient.updateProduct(productId, payload), HttpStatus.OK);
	}
	@RequestMapping(value = "/products/{productId}", method = RequestMethod.DELETE)
	public ResponseEntity<Product> deleteProduct(@PathVariable Long productId) {
		return new ResponseEntity<>(productClient.deleteProduct(productId), HttpStatus.OK);
	}

	/*
	------------CATEGORY-------------------
	*/
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Category>> getCategories() {
		return new ResponseEntity<Iterable<Category>>(categoryClient.getCategories(), HttpStatus.OK);
	}
	@RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.GET)
	public ResponseEntity<Category> getCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryClient.getCategory(categoryId), HttpStatus.OK);
	}
	@RequestMapping(value = "/categories", method = RequestMethod.POST)
	public ResponseEntity<Category> createCategory(@RequestBody Category payload) {
		return new ResponseEntity<Category>(categoryClient.createCategory(payload), HttpStatus.OK);
	}
	@RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.PUT)
	public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category payload) {
		return new ResponseEntity<>(categoryClient.updateCategory(categoryId, payload), HttpStatus.OK);
	}
	@RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.DELETE)
	public ResponseEntity<Category> deleteCategory(@PathVariable Long categoryId) {
		return new ResponseEntity<>(categoryClient.deleteCategory(categoryId), HttpStatus.OK);
	}

}