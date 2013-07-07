package com.prologic.idkey.objects;


public class Key{
	private final int no;
	private String id;
	private String date;
	private String hotelName;

	public Key(final int no) 
	{
		this.no = no;
		this.id = "";
		this.date = null;
		this.hotelName = "";
	}

	public Key(final int no,String roomName,String date,String hotelName)
	{
		this.no = no ;
		this.id = roomName;
		this.date = date;
		this.hotelName = hotelName;		
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
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	public int getNo() {
		return no;
	}

}
