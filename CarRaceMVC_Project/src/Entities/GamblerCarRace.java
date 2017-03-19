package Entities;

public class GamblerCarRace {
	private int gamblerId;
	private int raceNumber;
	private String carName;
	private int bet;
	public int getGamblerId() {
		return gamblerId;
	}
	
	public GamblerCarRace(int gamblerId, int raceNumber, String carName, int bet) {
		this.gamblerId = gamblerId;
		this.raceNumber = raceNumber;
		this.carName = carName;
		this.bet = bet;
	}
	
	public void setGamblerId(int gamblerId) {
		this.gamblerId = gamblerId;
	}
	
	public int getRaceNumber() {
		return raceNumber;
	}
	
	public void setRaceNumber(int raceNumber) {
		this.raceNumber = raceNumber;
	}
	
	public String getCarName() {
		return carName;
	}
	
	public void setCarName(String carName) {
		this.carName = carName;
	}
	
	public int getBet() {
		return bet;
	}
	
	public void setBet(int bet) {
		this.bet = bet;
	}
}
