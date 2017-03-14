package Entities;

enum RaceCommand {
	RaceConnect, RaceDisconnect, RaceStart, RaceChangeSpeed, RaceInitSettings;	
}

public class MessageRace {
	private RaceCommand command;
	private int raceNumber;
	private String[] carNames = new String[5];
	private int[] carSpeeds = new int[5];
	
}
