package Entities;

/**
 * Gambler class, holds information about gamblers.
 * @author Vitaly Ossipenkov
 * @author Omer Yaari
 *
 */
public class Gambler {
	private final int GAMBLER_INITIAL_BALANCE = 1000;
	private int id;
	private String name;
	private String password;
	private int balance;
	private boolean isOnline;

	/**
	 * Constructor for creating a new gambler (when gambler registers).
	 * @param id the gambler's id.
	 * @param name the gambler's user name.
	 * @param password the gambler's password.
	 */
	public Gambler(int id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
		balance = GAMBLER_INITIAL_BALANCE;
		isOnline = false;
	}
	
	/**
	 * Constructor for gambler when retrieving from database.
	 * @param id the gambler's id.
	 * @param name the gambler's user name.
	 * @param password the gambler's password.
	 * @param balance the gambler's balance.
	 */
	public Gambler(int id, String name, String password, int balance) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.balance = balance;
		isOnline = false;
	}
	
	/**
	 * Returns the gambler's name.
	 * @return the gambler's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the gambler's name to a new name.
	 * @param name the new name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the gambler's password.
	 * @return the gambler's password.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the gambler's password to a new password.
	 * @param password the new password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Returns the gambler's balance.
	 * @return the gambler's balance.
	 */
	public int getBalance() {
		return balance;
	}
	
	/**
	 * Sets the gambler's balance to a new balance.
	 * @param balance the new balance.
	 */
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	/**
	 * Returns the gambler's id number.
	 * @return the gambler's id number.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns whether the gambler is online or not (signed in or signed out).
	 * @return whether the gambler is online or not (signed in or signed out).
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * Sets the gambler's online status.
	 * @param isOnline the new status.
	 */
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
}
