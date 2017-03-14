package Entities;

enum MessageHeader {
	RaceMessage, GamblerMessage;
}

enum CommandType {
	RaceConnect, RaceDisconnect, RaceStart, RaceChangeSpeed, RaceInitSettings,
	GamblerConnect, GamblerDisconnect, GamblerLogin, GamblerLogout, GamblerBet;
}

public class Message {

	private MessageHeader header;
	//private String 
	
}
