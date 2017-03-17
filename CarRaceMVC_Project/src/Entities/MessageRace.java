package Entities;

import java.io.Serializable;

public class MessageRace implements Serializable {

	private static final long serialVersionUID = 4934362839558413711L;
	private RaceCommand command;
	private int raceNumber;
	private String[] carNames = new String[5];
	private int[] carSpeeds = new int[5];
	
	public RaceCommand getCommand() {
		return command;
	}
	
	public void setCommand(RaceCommand command) {
		this.command = command;
	}
	
	public int getRaceNumber() {
		return raceNumber;
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
