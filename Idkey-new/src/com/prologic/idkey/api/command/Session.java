package com.prologic.idkey.api.command;

public class Session 
{
	private int id;
	private String email;
	private String authToken;
	private String password;
	private static Session session = null;

	private Session() 
	{
		clearSession();
	}
	private void clearSession() 
	{
		id = -1;
		email = "";
		authToken = "";	
		password = "";
	}

	public static Session getInstance()
	{
		if(session == null)
			session = new Session();
		return session;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	



}
