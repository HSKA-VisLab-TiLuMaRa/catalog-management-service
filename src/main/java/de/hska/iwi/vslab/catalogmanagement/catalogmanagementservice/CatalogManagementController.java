package de.hska.iwi.vslab.catalogmanagement.catalogmanagementservice;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.LinkedList;
import java.util.List;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@EnableCircuitBreaker
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CatalogManagementController {

	@Autowired
	private ProductClient productClient;
	@Autowired
	private CategoryClient categoryClient;

	/*
	 * ------------PRODUCT-------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Product>> getProducts(@RequestParam Optional<String> name, @RequestParam Optional<Integer> categoryId) {
		if (name.isPresent()){
			return new ResponseEntity<Iterable<Product>>(productClient.getProducts(name.get()), HttpStatus.OK);
		} else if (categoryId.isPresent()) {
			return new ResponseEntity<Iterable<Product>>(productClient.getProducts(categoryId.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<Iterable<Product>>(productClient.getProducts(), HttpStatus.OK);
		}
	}
  //  OR hasRole('ROLE_USER')

	@RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
	public ResponseEntity<Product> getProduct(@PathVariable Long productId) {

		// check if id exists
		if (!productIdExists(productId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(productClient.getProduct(productId), HttpStatus.OK);
	}

	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public ResponseEntity<Product> createProduct(@RequestBody Product payload) {

		// check if category exists
		if (payload.getCategoryId() == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		if (!categoryIdExists(payload.getCategoryId())) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<Product>(productClient.createProduct(payload), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/{productId}", method = RequestMethod.PUT)
	public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product payload) {

		// check if object exists
		if (!productIdExists(productId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		// check if category exists
		if (!categoryIdExists(payload.getCategoryId())) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(productClient.updateProduct(productId, payload), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/{productId}", method = RequestMethod.DELETE)
	public ResponseEntity<Product> deleteProduct(@PathVariable Long productId) {

		// check if object exists
		if (!productIdExists(productId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(productClient.deleteProduct(productId), HttpStatus.OK);
	}

	/*
	 * ------------CATEGORY-------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/categories", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Category>> getCategories(@RequestParam Optional<String> name) {
		if (name.isPresent()){
			return new ResponseEntity<Iterable<Category>>(categoryClient.getCategories(name.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<Iterable<Category>>(categoryClient.getCategories(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.GET)
	public ResponseEntity<Category> getCategory(@PathVariable Long categoryId) {

		// check if category id exists
		if (!categoryIdExists(categoryId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(categoryClient.getCategory(categoryId), HttpStatus.OK);
	}

	@RequestMapping(value = "/categories", method = RequestMethod.POST)
	public ResponseEntity<Category> createCategory(@RequestBody Category payload) {

		return new ResponseEntity<Category>(categoryClient.createCategory(payload), HttpStatus.OK);
	}

	@RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.PUT)
	public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category payload) {

		// check if id already exists
		if (!categoryIdExists(payload.getId())) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(categoryClient.updateCategory(categoryId, payload), HttpStatus.OK);
	}

	@RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.DELETE)
	public ResponseEntity<Category> deleteCategory(@PathVariable Long categoryId) {

		// check if id exists
		if (!categoryIdExists(categoryId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		// check if products with this id exist
		// overwrite with null
		Iterable<Product> allProducts = productClient.getProducts();
		for (Product product : allProducts) {
			if (product == null)
				continue;

			Long thisCategoryId = Long.valueOf(product.getCategoryId());
			if (thisCategoryId != null && thisCategoryId.longValue() == categoryId) {
				product.setCategoryId(null);
			}
		}

		return new ResponseEntity<>(categoryClient.deleteCategory(categoryId), HttpStatus.OK);
	}

	/*
	 * ---------------SEARCH-----------------
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Object>> search(@RequestParam String searchTerm) {

		Iterable<Product> allProducts = productClient.getProducts();
		Iterable<Category> allCategories = categoryClient.getCategories();
		List<Object> toReturn = new LinkedList<Object>();

		for (Product toCheck : allProducts) {
			if (toCheck.getName().contains(searchTerm)) {
				toReturn.add(toCheck);
			}
		}
		for (Category toCheck : allCategories) {
			if (toCheck.getName().contains(searchTerm)) {
				toReturn.add(toCheck);
			}
		}

		return new ResponseEntity<Iterable<Object>>(toReturn, HttpStatus.OK);
	}

	/*
	 * PRIVATE METHODS
	 */
	private boolean productIdExists(long idToCheck) {
		return productClient.getProduct(idToCheck) != null;
	}

	private boolean categoryIdExists(long idToCheck) {
		return categoryClient.getCategory(idToCheck) != null;
	}

}
