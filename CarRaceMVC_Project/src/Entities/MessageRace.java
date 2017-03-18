package Entities;

import java.io.Serializable;

public class MessageRace implements Serializable {

	private static final long serialVersionUID = 4934362839558413711L;
	private RaceCommand command;
	private int raceNumber;
	private String[] carNames, carMakes, carSizes, carColors, carTypes;
	private int[] carSpeeds;
	private boolean status;
	
	/**
	 * Race handler uses this constructor to supply a new race controller with all available car names.
	 * Race controller uses this constructor to reply to the handler with 5 chosen car names.
	 * @param command the race command.
	 * @param raceNumber the race number.
	 * @param carNames the array of car names.
	 * @param status the message's status.
	 */
	public MessageRace(RaceCommand command, int raceNumber, String[] carNames, boolean status) {
		this.command = command;
		this.raceNumber = raceNumber;
		this.carNames = carNames;
		this.status = status;
	}
	
	/**
	 * Race handler uses this constructor to build messages that 
	 * supply the cars' info to the controller.
	 * @param command the race command.
	 * @param raceNumber the race's number.
	 * @param carNames the array of car names.
	 * @param carMakes the array of car makes.
	 * @param carSizes the array of car sizes.
	 * @param carColors the array of car colours.
	 * @param carTypes the array of car types.
	 * @param status the message's status.
	 */
	public MessageRace(RaceCommand command, int raceNumber, String[] carNames, 
			String[] carMakes, String[] carSizes, String[] carColors, String[] carTypes, 
			boolean status) {
		this.command = command;
		this.carNames = carNames;
		this.carMakes = carMakes;
		this.carSizes = carSizes;
		this.carColors = carColors;
		this.carTypes = carTypes;
		this.status = status;
	}
	
	/**
	 * Race controller uses this constructor to build messages that notify the model of speed changes.
	 * @param command the race command.
	 * @param raceNumber the race number.
	 * @param carNames the array of car names.
	 * @param carSpeeds the array of car speeds.
	 * @param status the message's status.
	 */
	public MessageRace(RaceCommand command, int raceNumber, String[] carNames, 
			int[] carSpeeds, boolean status) {
		this.command = command;
		this.raceNumber = raceNumber;
		this.carNames = carNames;
		this.carSpeeds = carSpeeds;
		this.status = status;
	}
	
	public MessageRace(RaceCommand command, boolean status) {
		this.command = command;
		this.status = status;
	}
	
	public RaceCommand getCommand() {
		return command;
	}
	
	public void setCommand(RaceCommand command) {
		this.command = command;
	}
	
	public int getRaceNumber() {
		return raceNumber;
	}
	
	public String[] getCarMakes() {
		return carMakes;
	}

	public void setCarMakes(String[] carMakes) {
		this.carMakes = carMakes;
	}

	public String[] getCarSizes() {
		return carSizes;
	}

	public void setCarSizes(String[] carSizes) {
		this.carSizes = carSizes;
	}

	public String[] getCarColors() {
		return carColors;
	}

	public void setCarColors(String[] carColors) {
		this.carColors = carColors;
	}

	public String[] getCarTypes() {
		return carTypes;
	}

	public void setCarTypes(String[] carTypes) {
		this.carTypes = carTypes;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setRaceNumber(int raceNumber) {
		this.raceNumber = raceNumber;
	}
	
	public String[] getCarNames() {
		return carNames;
	}
	public void setCarNames(String[] carNames) {
		this.carNames = carNames;
	}
	
	public int[] getCarSpeeds() {
		return carSpeeds;
	}
	
	public void setCarSpeeds(int[] carSpeeds) {
		this.carSpeeds = carSpeeds;
	}
	

	
}
