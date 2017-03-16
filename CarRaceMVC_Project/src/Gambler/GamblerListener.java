package Gambler;

import Entities.Gambler;

public interface GamblerListener {
	void loginSuccess(Gambler gambler);
	void betPlaceSuccess(int newBalance);
	void betPlaceFailed();
	void newRaceStart();
	void raceEnded();
	void registerSuccess(boolean success);
}
