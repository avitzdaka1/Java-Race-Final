package Entities;

import java.io.Serializable;

/**
 * Message between gambler client and gambler handler on the server.
 * @author Vitaly Ossipenkov
 * @author Omer Yaari
 *
 */
public class MessageGambler implements Serializable {

	private static final long serialVersionUID = 4074618552007098484L;
	private GamblerCommand command;
	private String username, password;
	private String[] carNames;
	private int bet, balance, id;
	private int[] raceNumbers;

	private boolean status;
	
	/**
	 * "Short" constructor used mainly by the handler to notify of failures in login / registration.
	 * @param command the command that the message holds.
	 * @param status the status of the command (fail / success).
	 */
	public MessageGambler(GamblerCommand command, boolean status) {
		this.command = command;
		this.status = status;
	}
	
	/**
	 * Constructor used to hold complete gambler information.
	 * @param command the command that the message holds.
	 * @param username the gambler's user name.
	 * @param password the gambler's password.
	 * @param balance the gambler's balance.
	 * @param id the gambler's id.
	 * @param status the status of the command (fail / success).
	 */
	public MessageGambler(GamblerCommand command, String username, String password, int balance, int id,
			boolean status) {
		this.command = command;
		this.username = username;
		this.password = password;
		this.balance = balance;
		this.id = id;
		this.status = status;
	}

	/**
	 * "Short" constructor used to hold enough information for login / registering a new user.
	 * @param command the command that the message holds.
	 * @param username the gambler's user name.
	 * @param password the gambler's password.
	 */
	public MessageGambler(GamblerCommand command, String username, String password) {
		this.command = command;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Constructor used by gambler to place bets.
	 * @param command the gambler message command.
	 * @param username the gambler's user name.
	 * @param raceNumber the race number.
	 * @param carName the car's name.
	 * @param bet the bet.
	 */
	public MessageGambler(GamblerCommand command, int id, int[] raceNumbers, String[] carNames, int bet) {
		this.command = command;
		this.id = id;
		this.raceNumbers = raceNumbers;
		this.carNames = carNames;
		this.bet = bet;
	}
	
	/**
	 * Returns the command thats inside the message.
	 * @return the command thats inside the message.
	 */
	public GamblerCommand getCommand() {
		return command;
	}
	
	/**
	 * Sets the command thats inside the message.
	 * @param command the new command.
	 */
	public void setCommand(GamblerCommand command) {
		this.command = command;
	}
	
	/**
	 * Returns the gambler's user name.
	 * @return the gambler's user name.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Sets the gambler's user name thats inside the message.
	 * @param username the new user name.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Returns the gambler's password thats inside the message.
	 * @return the gambler's password thats inside the message.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the gambler's password thats inside the message.
	 * @param password the new password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Returns the name of the car thats inside the message.
	 * @return the name of the car thats inside the message.
	 */
	public String[] getCarName() {
		return carNames;
	}
	
	/**
	 * Sets the name of the car thats inside the message.
	 * @param carName the new car name.
	 */
	public void setCarName(String[] carNames) {
		this.carNames = carNames;
	}
	
	/**
	 * Returns the number of the race thats inside the message.
	 * @return the number of the race thats inside the message.
	 */
	public int[] getRaceNumber() {
		return raceNumbers;
	}
	
	/**
	 * Sets the number of the race thats inside the message.
	 * @param raceNumber the new race number.
	 */
	public void setRaceNumber(int[] raceNumbers) {
		this.raceNumbers = raceNumbers;
	}
	
	/**
	 * Returns the gambler's bet thats inside the message.
	 * @return the gambler's bet thats inside the message.
	 */
	public int getBet() {
		return bet;
	}
	
	/**
	 * Sets the gambler's bet thats inside the message.
	 * @param bet the new bet.
	 */
	public void setBet(int bet) {
		this.bet = bet;
	}
	
	/**
	 * Returns the gambler's balance thats inside the message.
	 * @return the gambler's balance thats inside the message.
	 */
	public int getBalance() {
		return balance;
	}
	
	/**
	 * Sets the gambler's balance thats inside the message.
	 * @param balance the new balance.
	 */
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	/**
	 * Returns the message's status.
	 * @return the message's status.
	 */
	public boolean getStatus() {
		return status;
	}
	
	/**
	 * Sets the message's status.
	 * @param status the new status.
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	/**
	 * Returns the id of the gambler thats inside the message.
	 * @return the id of the gambler thats inside the message.
	 */
	public int getId() {
		return id;
	}
}
