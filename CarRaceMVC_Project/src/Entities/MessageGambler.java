package Entities;

import java.io.Serializable;

//	Message between gambler client and gambler handler on the server.
public class MessageGambler implements Serializable {

	private static final long serialVersionUID = 4074618552007098484L;
	private GamblerCommand command;
	private String username, password, carName;
	private int raceNumber, bet, balance, id;

	private boolean status;
	
	
	public MessageGambler(GamblerCommand command, boolean status) {
		this.command = command;
		this.status = status;
	}
	
	
	public MessageGambler(GamblerCommand command, String username, String password, int balance, int id,
			boolean status) {
		this.command = command;
		this.username = username;
		this.password = password;
		this.balance = balance;
		this.id = id;
		this.status = status;
	}


	public MessageGambler(GamblerCommand command, String username, String password) {
		this.command = command;
		this.username = username;
		this.password = password;
	}
	
	
	
	public GamblerCommand getCommand() {
		return command;
	}
	public void setCommand(GamblerCommand command) {
		this.command = command;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCarName() {
		return carName;
	}
	public void setCarName(String carName) {
		this.carName = carName;
	}
	public int getRaceNumber() {
		return raceNumber;
	}
	public void setRaceNumber(int raceNumber) {
		this.raceNumber = raceNumber;
	}
	public int getBet() {
		return bet;
	}
	public void setBet(int bet) {
		this.bet = bet;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
}
