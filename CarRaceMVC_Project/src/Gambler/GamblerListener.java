package Gambler;

import Entities.Gambler;

/**
 * Event interface used by the gambler client to notify the gambler view of updates.
 * @author Vitaly Ossipenkov
 * @author Omer Yaari
 *
 */
public interface GamblerListener {
	void loginSuccess(Gambler gambler);
	void betPlaceSuccess(int newBalance, boolean success);
	void newRaceStart();
	void raceEnded();
	void registerSuccess(boolean success);
}
