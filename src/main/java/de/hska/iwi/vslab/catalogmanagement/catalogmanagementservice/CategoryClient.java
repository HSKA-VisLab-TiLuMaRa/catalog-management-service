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
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "getCategoriesCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Iterable<Category> getCategories() {
		Collection<Category> categories = new HashSet<Category>();
		Category[] tmpcategories = restTemplate.getForObject("http://category-service/categories", Category[].class);
		Collections.addAll(categories, tmpcategories);
		categoryCache.clear();
		categories.forEach(u -> categoryCache.put(u.getId(), u));
		return categories;
	}

	@HystrixCommand(fallbackMethod = "getCategoryCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Category getCategory(Long categoryId) {
		Category tmpcategory = restTemplate.getForObject("http://category-service/categories/" + categoryId, Category.class);
		categoryCache.putIfAbsent(categoryId, tmpcategory);
		return tmpcategory;
	}

	public Iterable<Category> getCategoriesCache() {
		return categoryCache.values();
	}

	public Category getCategoryCache(Long categoryId) {
		return categoryCache.getOrDefault(categoryId, new Category());
	}

}