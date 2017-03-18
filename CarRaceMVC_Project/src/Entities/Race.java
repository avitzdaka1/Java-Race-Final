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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Race other = (Race) obj;
		if (number != other.number)
			return false;
		return true;
	}
	
}
