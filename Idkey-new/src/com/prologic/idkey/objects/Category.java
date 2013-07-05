package com.prologic.idkey.objects;

public class Category 
{
	private final int id;
	private String name;
	private int count;
	
	public Category(final int id) 
	{
		this.id = id;
		this.name = "";
		this.count = 0;
	
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getId() {
		return id;
	}
	
	

}
