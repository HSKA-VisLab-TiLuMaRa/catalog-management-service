package de.hska.iwi.vslab.catalogmanagement.catalogmanagementservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableCircuitBreaker
public class CatalogManagementController {

	@Autowired
    private ProductClient productClient;
    @Autowired  
    private CategoryClient categoryClient;

	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Product>> getProducts() {
		return new ResponseEntity<Iterable<Product>>(productClient.getProducts(), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
	public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
		return new ResponseEntity<>(productClient.getProduct(productId), HttpStatus.OK);
    }
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Category>> getCategories() {
		return new ResponseEntity<Iterable<Category>>(categoryClient.getCategories(), HttpStatus.OK);
	}

	@RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.GET)
	public ResponseEntity<Category> getCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryClient.getCategory(categoryId), HttpStatus.OK);
    }
	

}