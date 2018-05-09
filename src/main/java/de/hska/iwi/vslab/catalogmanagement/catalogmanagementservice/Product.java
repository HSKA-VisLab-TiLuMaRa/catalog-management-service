package de.hska.iwi.vslab.catalogmanagement.catalogmanagementservice;

public class Product {
	private Long id;
	private String name;
	private Integer price;
	private Integer categoryId;
	private String details;

	public Product(){}
			
	public Product(Long id, String name, Integer price, Integer categoryId, String details) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.categoryId = categoryId;
		this.details = details;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + "]";
	}

}