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
public class CategoryClient {

	private final Map<Long, Category> categoryCache = new LinkedHashMap<Long, Category>();

	@Autowired
	private RestTemplate categoryRestTemplate;

	@LoadBalanced
	@Bean
	public RestTemplate categoryRestTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}


	/*
	-------GET---------
	*/
	@HystrixCommand(fallbackMethod = "getCategoriesCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Iterable<Category> getCategories() {
		Collection<Category> categories = new HashSet<Category>();
		Category[] tmpcategories = categoryRestTemplate.getForObject("http://category-service/categories", Category[].class);
		Collections.addAll(categories, tmpcategories);
		categoryCache.clear();
		categories.forEach(u -> categoryCache.put(u.getId(), u));
		return categories;
	}

	@HystrixCommand(fallbackMethod = "getCategoryCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Category getCategory(Long categoryId) {

		if (categoryId == null) {
			return null;
		}

		Category tmpcategory = restTemplate.getForObject("http://category-service/categories/" + categoryId,
				Category.class);
		categoryCache.putIfAbsent(categoryId, tmpcategory);
		return tmpcategory;
	}

	/*
	-------POST---------
	*/
	@HystrixCommand(fallbackMethod = "createCategoryFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Category createCategory(Category payload) {
		Category tmpcategory = categoryRestTemplate.postForObject("http://category-service/categories", payload,
				Category.class);
		return tmpcategory;
	}

	/*
	-------PUT---------
	*/
	@HystrixCommand(fallbackMethod = "updateCategoryFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Category updateCategory(Long categoryId, Category payload) {
		categoryRestTemplate.put("http://category-service/categories/" + categoryId, payload);
		return new Category();
	}

	/*
	-------DELETE---------
	*/
	@HystrixCommand(fallbackMethod = "deleteCategoryFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Category deleteCategory(Long categoryId) {
		categoryRestTemplate.delete("http://category-service/categories/" + categoryId);
		return new Category();
	}

	public Iterable<Category> getCategoriesCache() {
		return categoryCache.values();
	}

	public Category getCategoryCache(Long categoryId) {
		return categoryCache.getOrDefault(categoryId, new Category());
	}

	public Category createCategoryFallback(Category payload){
		return payload;
	}

	public Category updateCategoryFallback(Long categoryId, Category payload){
		return payload;
	}

	public Category deleteCategoryFallback(Long categoryId){
		return new Category();
	}


}
