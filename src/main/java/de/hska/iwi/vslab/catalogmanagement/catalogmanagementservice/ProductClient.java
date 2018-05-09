package de.hska.iwi.vslab.catalogmanagement.catalogmanagementservice;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

	private final Map<Long, Product> productCache = new LinkedHashMap<Long, Product>();

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Autowired
	private RestTemplate restTemplate;

	/*
	-------GET---------
	*/
	@HystrixCommand(fallbackMethod = "getProductsCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Iterable<Product> getProducts() {
		Collection<Product> products = new HashSet<Product>();
		Product[] tmpproducts = restTemplate.getForObject("http://product-service/products", Product[].class);
		Collections.addAll(products, tmpproducts);
		productCache.clear();
		products.forEach(u -> productCache.put(u.getId(), u));
		return products;
	}
	@HystrixCommand(fallbackMethod = "getProductCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Product getProduct(Long productId) {
		Product tmpproduct = restTemplate.getForObject("http://product-service/products/" + productId, Product.class);
		productCache.putIfAbsent(productId, tmpproduct);
		return tmpproduct;
	}

	/*
	-------POST---------
	*/
	@HystrixCommand(fallbackMethod = "createProductFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Product createProduct(Product payload) {
		Product tmpproduct = restTemplate.postForObject("http://product-service/products", payload,
				Product.class);
		return tmpproduct;
	}

	/*
	-------PUT---------
	*/
	@HystrixCommand(fallbackMethod = "updateProductFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Product updateProduct(Long productId, Product payload) {
		restTemplate.put("http://product-service/products/" + productId, payload);
		return new Product();
	}

	/*
	-------DELETE---------
	*/
	@HystrixCommand(fallbackMethod = "deleteProductFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Product deleteProduct(Long productId) {
		restTemplate.delete("http://product-service/products/" + productId);
		return new Product();
	}

	public Iterable<Product> getProductsCache() {
		return productCache.values();
	}

	public Product getProductCache(Long productId) {
		return productCache.getOrDefault(productId, new Product());
	}

}