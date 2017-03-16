package Gambler;

import Entities.Gambler;

public interface GamblerListener {
	void loginSuccessful(Gambler gambler);
	void loginUnsuccessful();
	void betPlaceSuccess(int newBalance);
	void betPlaceFailed();
	void newRaceStart();
	void raceEnded();
}
