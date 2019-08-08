package io.github.cloudoas.excelloader.model;

public class Department {
	public String name;
	private Double budget;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getBudget() {
		return budget;
	}
	public void setBudget(Double budget) {
		this.budget = budget;
	}
	
	@Override
	public String toString() {
		return "Department [name=" + name + ", budget=" + budget + "]";
	}
}
