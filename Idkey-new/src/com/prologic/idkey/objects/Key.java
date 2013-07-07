package com.prologic.idkey.objects;


public class Key{
	private final int no;
	private String id;
	private String date;
	private String category;

	public Key(final int no) 
	{
		this.no = no;
		this.id = "";
		this.date = null;
		this.category = "";
	}

	public Key(final int no,String roomName,String date,String category)
	{
		this.no = no ;
		this.id = roomName;
		this.date = date;
		this.category = category;		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCategory()
	{
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getNo() {
		return no;
	}

}
