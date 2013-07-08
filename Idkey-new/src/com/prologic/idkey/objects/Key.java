package com.prologic.idkey.objects;


public class Key
{
	private final int id;
	private String name;
	private String iqeToken;
	private String scanUrl;
	private int categoryId;
	private String categoryName;
	private String createDate;	

	public Key(final int id) 
	{
		this.id = id;
		this.name = "";
		this.iqeToken = "";
		this.categoryId = -1;
		categoryName = "";
		createDate = "";
	}

	public Key(final int id,String name,String date,String categoryName)
	{
		this.id = id ;
		this.name = name;
		this.createDate = date;
		this.categoryName = categoryName;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIqeToken() {
		return iqeToken;
	}

	public void setIqeToken(String iqeToken) {
		this.iqeToken = iqeToken;
	}

	public String getScanUrl() {
		return scanUrl;
	}

	public void setScanUrl(String scanUrl) {
		this.scanUrl = scanUrl;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}
	
	
	
	/*private final int no;
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
	}*/

}
