package Entities;

import java.io.Serializable;

enum RaceCommand {
	RaceConnect, RaceDisconnect, RaceStart, RaceChangeSpeed, RaceInitSettings;	
}

public class MessageRace implements Serializable {

	private static final long serialVersionUID = 4934362839558413711L;
	private RaceCommand command;
	private int raceNumber;
	private String[] carNames = new String[5];
	private int[] carSpeeds = new int[5];
	
}
