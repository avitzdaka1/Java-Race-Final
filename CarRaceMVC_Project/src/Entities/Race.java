package Entities;

import java.sql.Date;

public class Race {
	private int number;
	private Date date;
	private int state;
	private int totalBets;
	
	public Race(int number, Date date) {
		this.number = number;
		this.date = date;
		state = 0;
		totalBets = 0;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getTotalBets() {
		return totalBets;
	}
	public void setTotalBets(int totalBets) {
		this.totalBets = totalBets;
	}
	public int getNumber() {
		return number;
	}
	public Date getDate() {
		return date;
	}
	
}
