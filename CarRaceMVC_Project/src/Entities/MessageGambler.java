package Entities;

enum GamblerCommand {
	GamblerConnect, GamblerDisconnect, GamblerLogin, GamblerLogout, GamblerBet;
}

//	Message between gambler client and gambler handler on the server.
public class MessageGambler {

	private GamblerCommand command;
	private String username, password, carName;
	private int raceNumber, bet, balance;
	private boolean status;
	
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
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
}
