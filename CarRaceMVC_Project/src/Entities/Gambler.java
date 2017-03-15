package Entities;

public class Gambler {
	private final int GAMBLER_INITIAL_BALANCE = 1000;
	private int id;
	private String name;
	private String password;
	private int balance;
	private boolean isOnline;

	//	Constructor for creating a new gambler (when gambler registers).
	public Gambler(int id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
		balance = GAMBLER_INITIAL_BALANCE;
		isOnline = false;
	}
	
	//	Constructor for gambler when retrieving from database.
	public Gambler(int id, String name, String password, int balance) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.balance = balance;
		isOnline = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
}
