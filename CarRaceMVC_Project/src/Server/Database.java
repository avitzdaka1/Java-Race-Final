	package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import Entities.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.util.Callback;

public class Database {

	// DB connecting information
	private final String RACE_DB = "RACE_DB.txt";

	/**
	 * Database constructor, if the database is empty, it creates it.
	 */
	public Database() {
		if (!checkDBExists())
			createNewDB();
	}
	
	/**
	 * Checks if database tables exist (car table must always be not empty).
	 * @return whether the database exists or not.
	 * @exception Exception
	 */
	private boolean checkDBExists() {
		String query = "SELECT * FROM Car";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott",
					"tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							return true;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Creates and inserts tables to the (empty) database.
	 * @return whether the method succeeded or not.
	 * @exception Exception
	 */
	public boolean createNewDB () {
		try (FileInputStream fstream = new FileInputStream(RACE_DB)) {
			try (DataInputStream in = new DataInputStream(fstream)) {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
					Class.forName("com.mysql.jdbc.Driver");
					try (Connection dbConnection = DriverManager
							.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
						try (Statement dbStatement = dbConnection.createStatement()) {
							String strLine = "", strLine1 = "";
							// Read File Line By Line

							while ((strLine = br.readLine()) != null) {
								if (strLine != null && !strLine.trim().equals("")) {
									if ((strLine.trim().indexOf("/*") < 0) && (strLine.trim().indexOf("*/") < 0)) {
										if (strLine.indexOf(';') >= 0) {
											strLine1 += strLine;
											System.out.println(strLine1);
											dbStatement.execute(strLine1);
											strLine1 = "";
										} else
											strLine1 += strLine;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * Updates a race result when a race has finished.
	 * @param raceNumber the race's number.
	 * @param carName the car's name.
	 * @param position the car's position in the race.
	 * @return whether the update succeeded.
	 */
	public synchronized boolean updateCarRaceResult(int raceNumber, String carName, int position) {
		String query = "UPDATE CarRaceResult " + 
					"SET position = ? " + 
					"WHERE raceNumber = ? " +
					"AND carName = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					int raceTotalBets = getRaceTotalBets(raceNumber);
					if (raceTotalBets != -1) {
						dbPrepStatement.setInt(1, position);
						dbPrepStatement.setInt(2, raceNumber);
						dbPrepStatement.setString(3, carName);
						dbPrepStatement.executeUpdate();
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns all available races that haven't started yet (but are betable).
	 * @return array of races that a gambler can bet on.
	 */
	public ArrayList<Integer> getHoldingRaceNumbers() {
		String query = "SELECT Race.number " + 
					"FROM Race " + 
					"WHERE Race.state BETWEEN 0 AND 3";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						ArrayList<Integer> raceNumbers = new ArrayList<>();
						while (resultSet.next()) {
							raceNumbers.add(resultSet.getInt(1));
						}
						return raceNumbers;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the number of the race that is ready and has most bets.
	 * @return race number.
	 */
	public ArrayList<GamblerCarRace> getCurrentGamblerCarRaceRows() {
		String query = "SELECT GamblerCarRace.gamblerId, GamblerCarRace.raceNumber, GamblerCarRace.carName, GamblerCarRace.bet " + 
					"FROM GamblerCarRace, Race " + 
					"WHERE Race.number = GamblerCarRace.raceNumber " +
					"AND Race.state BETWEEN 0 AND 3";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						ArrayList<GamblerCarRace> resultList = new ArrayList<>();
						while (resultSet.next()) {
							GamblerCarRace gcr = new GamblerCarRace(resultSet.getInt(1), resultSet.getInt(2), 
									resultSet.getString(3), resultSet.getInt(4));
							resultList.add(gcr);
						}
						return resultList;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the number of the race that is ready and has most bets.
	 * @return race number.
	 */
	public HashMap<Integer, Integer> getRaceToStart() {
		String query = "SELECT GamblerCarRace.raceNumber, GamblerCarRace.carName " + 
					"FROM GamblerCarRace, Race " + 
					"WHERE Race.number = GamblerCarRace.raceNumber " +
					"AND Race.state BETWEEN 0 AND 3";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						HashMap<Integer, Integer> resultList = new HashMap<>();
						while (resultSet.next()) {
							if (resultList.containsKey(resultSet.getInt(1))) {
								Integer carsCount = resultList.get(resultSet.getInt(1));
								carsCount++;
							}
							else 
								resultList.put(resultSet.getInt(1), new Integer(1));
						}
						return resultList;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * Returns a given race's cars.
	 * @return arraylist of car names.
	 */
	public ArrayList<String> getCarsInRace(int raceNumber) {
		String query = "SELECT CarRaceResult.carName " + 
					"FROM CarRaceResult " + 
					"WHERE CarRaceResult.raceNumber = " + raceNumber;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						ArrayList<String> carNames = new ArrayList<>();
						while (resultSet.next()) {
							carNames.add(resultSet.getString(1));
						}
						return carNames;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns all car names.
	 * @return the string array of car names.
	 */
	public String[] getCarNames() {
		String query = "SELECT Car.name " + "FROM Car ";
		ArrayList<String> cars = new ArrayList<>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott",
					"tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						while (resultSet.next())
							cars.add(resultSet.getString(1));
						String[] carsArr = (String[]) cars.toArray(new String[cars.size()]);
						return carsArr;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns car's properties given its name.
	 * @return the car object that holds the car and its props.
	 */
	public Car getCarProps(String carName) {
		String query = "SELECT Car.make, Car.size, Car.color, Car.type " + 
					"FROM Car " +
					"WHERE Car.name = '" + carName + "'";
		Car car = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott",
					"tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next()) {
							car = new Car(carName, resultSet.getString(1), 
									resultSet.getString(2), resultSet.getString(3), 
									resultSet.getString(4));
						}
						return car;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the last race number.
	 * @return the last race number.
	 * @exception Exception
	 */
	public int getLastRaceNumber() {
		String query = "SELECT Race.number " + 
					"FROM Race " + 
					"ORDER BY Race.number DESC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next()) {
							int raceNumber = resultSet.getInt(1);
							resultSet.close();
							return raceNumber;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Returns the last gambler's id.
	 * @return the last gambler's id.
	 * @exception Exception
	 */
	public int getLastGamblerId() {
		String query = "SELECT Gambler.id " + 
					"FROM Gambler " + 
					"ORDER BY Gambler.id DESC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							return resultSet.getInt(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Returns gambler details using given gambler id.
	 * @param gamblerName the gambler's name to search for.
	 * @return gambler details.
	 * @exception Exception
	 */
	public Gambler getGamblerDetails(int gamblerId) {
		String query = "SELECT * " + 
					"FROM Gambler " + 
					"WHERE Gambler.id = " + gamblerId;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							return new Gambler(resultSet.getInt(1), resultSet.getString(2), 
									resultSet.getString(3), resultSet.getInt(4));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Updates a given gambler bet with a new value.
	 * @param raceNumber the race to bet in.
	 * @param gamblerId the gambler that bet.
	 * @param carName the car that the gambler bet on.
	 * @param bet the bet.
	 * @return whether the update succeeded.
	 */
	public synchronized boolean updateGamblerBet(int raceNumber, int gamblerId, 
			String carName, int bet) {
		String query = "UPDATE GamblerCarRace " + 
					"SET bet = ? " + 
					"WHERE number = ? " +
					"AND gamblerId = ? " + 
					"AND carName = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, bet);
					dbPrepStatement.setInt(2, raceNumber);
					dbPrepStatement.setInt(3, gamblerId);
					dbPrepStatement.setString(4, carName);
					dbPrepStatement.executeUpdate();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns gambler details using given gambler name.
	 * @param gamblerName the gambler's name to search for.
	 * @return gambler details.
	 * @exception Exception
	 */
	public Gambler getGamblerDetails(String gamblerName) {
		String query = "SELECT * " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "'";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							return new Gambler(resultSet.getInt(1), resultSet.getString(2), 
									resultSet.getString(3), resultSet.getInt(4));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Finds out of a given gambler already exists in database (checks name).
	 * @param gamblerName the gambler's name to search for.
	 * @return if the gambler already exists.
	 * @exception Exception
	 */
	public boolean gamblerExists(String gamblerName) {
		String query = "SELECT Gambler.name " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "'";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							if (resultSet.getString(1).equals(gamblerName))
								return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Finds out if a given gambler (name and password) is online.
	 * @param gamblerName the gambler's name to search for.
	 * @return whether gambler is online or not.
	 * @exception Exception
	 */
	public boolean gamblerOnline(String gamblerName) {
		String query = "SELECT Gambler.name, Gambler.isOnline " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "' ";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							if (resultSet.getBoolean(2))
								return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checks a given gambler's login credentials, and if the gambler is currently online.
	 * @param gamblerName the gambler's user name.
	 * @param gamblerPassword the gambler's password.
	 * @return whether the gambler can login or not.
	 * @exception Exception
	 */
	public boolean checkGamblerAuth(String gamblerName, String gamblerPassword) {
		String query = "SELECT Gambler.name, Gambler.password, Gambler.isOnline " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "' " + 
					"AND Gambler.password = '" + gamblerPassword + "'";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next()) {
							if (!resultSet.getBoolean(3))
								return true;
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Get gambler balance given gambler id.
	 * @param gamblerId the gambler id.
	 * @return the gambler's balance.
	 */
	public int getGamblerBalance(int gamblerId) {
		String query = "SELECT Gambler.balance " + 
					"FROM Gambler " + 
					"WHERE Gambler.id = " + gamblerId;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/javarace?useSSL=false",
					"scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							return resultSet.getInt(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Checks if gambler has enough balance to afford given bet,
	 * if he does, bet on given car in a given race.
	 * @param gambler the gambler that bets.
	 * @param race the race the gamblers bets in.
	 * @param car the car the gamblers bets on.
	 * @param bet the gambler's bet.
	 * @return whether the bet has been placed or not.
	 * @exception Exception
	 */
	public boolean gamblerBet(int gamblerId, int raceNum, String carName, int bet) {
		String query = "SELECT Gambler.balance " + 
					"FROM Gambler " +
					"WHERE Gambler.id = " + gamblerId;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next()) {
							int balance = resultSet.getInt(1);
							// If gambler's balance is equal or greater than bet.
							if (bet <= balance) {
								// Check if gambler already bet on that car.
								int oldBet = checkCarBetExists(raceNum, gamblerId, carName);
								if (oldBet != -1) {
									updateGamblerBet(raceNum, gamblerId, carName, bet + oldBet);
									int raceTotalBets = getRaceTotalBets(raceNum);
									updateRaceTotalBets(raceNum, bet + raceTotalBets);
								}
								else {
									placeGamblerBet(gamblerId, raceNum, carName, bet);
									int raceTotalBets = getRaceTotalBets(raceNum);
									updateRaceTotalBets(raceNum, bet + raceTotalBets);
								}
								updateGamblerBalance(gamblerId, balance - bet);
								return true;
							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Places a gambler bet on a given car and race.
	 * @param gamblerId the gambler that bets.
	 * @param raceNumber the race the gambler bets in.
	 * @param carName the car the gambler bets on.
	 * @param bet the bet.
	 * @return whether the bet has been placed or not.
	 * @exception Exception
	 */
	public synchronized boolean placeGamblerBet(int gamblerId, int raceNumber, String carName, int bet) {
		String query = "INSERT INTO GamblerCarRace " + 
					"(gamblerId, raceNumber, carName, bet) " + 
					"VALUES ( ?, ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, gamblerId);
					dbPrepStatement.setInt(2, raceNumber);
					dbPrepStatement.setString(3, carName);
					dbPrepStatement.setInt(4, bet);
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Inserts new gambler-race result (revenue from race).
	 * @param gamblerId the result's gambler id.
	 * @param raceNumber the result's race number.
	 * @param revenue the gambler revenue of the race.
	 * @return whether the insertion succeeded.
	 * @exception Exception
	 */
	public synchronized boolean insertGamblerRaceResult(int gamblerId, int raceNumber, int revenue) {
		String query = "INSERT INTO GamblerRaceResult " + 
					"(gamblerId, raceNumber, revenue) " + 
					"VALUES ( ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, gamblerId);
					dbPrepStatement.setInt(2, raceNumber);
					dbPrepStatement.setInt(3, revenue);
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Inserts new car-race result (car's position in race).
	 * @param raceNumber the result's race number.
	 * @param carName the result's car name.
	 * @param position the position of the car, starts from 1.
	 * @return whether the insertion succeeded.
	 * @exception exception
	 */
	public synchronized boolean insertCarRaceResult(int raceNumber, String carName, int position) {
		String query = "INSERT INTO CarRaceResult " + 
					"(raceNumber, carName, position) " + 
					"VALUES ( ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott",
					"tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, raceNumber);
					dbPrepStatement.setString(2, carName);
					dbPrepStatement.setInt(3, position);
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Updates a given race's total bets.
	 * @param raceNumber the race number to update.
	 * @param bet the bet to add.
	 * @return whether update succeeded.
	 * @exception Exception
	 */
	public synchronized boolean updateRaceTotalBets(int raceNumber, int bet) {
		String query = "UPDATE Race " + 
					"SET totalBets = ? " + 
					"WHERE number = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, bet);
					dbPrepStatement.setInt(2, raceNumber);
					dbPrepStatement.executeUpdate();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Updates a given race's state.
	 * @param raceNumber the race number to update.
	 * @param state the state to update.
	 * @return whether update succeeded.
	 * @exception Exception
	 */
	public synchronized boolean updateRaceState(int raceNumber, int state) {
		String query = "UPDATE Race " + 
					"SET state = ? " + 
					"WHERE number = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, state);
					dbPrepStatement.setInt(2, raceNumber);
					dbPrepStatement.executeUpdate();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
		
	/**
	 * Retrieves a given race's total bets (using race number).
	 * @param raceNumber the race to look for.
	 * @return the total amount of bets of a given race.
	 * @exception Exception
	 */
	public int getRaceTotalBets(int raceNumber) {
		String query = "SELECT Race.totalBets " + 
					"FROM Race " + 
					"WHERE Race.number = " + raceNumber;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							return resultSet.getInt(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
		
	/**
	 * Updates a given gambler's balance.
	 * @param gambler the gambler to update.
	 * @param balance the balance to update.
	 * @return whether update succeeded.
	 * @exception Exception
	 */
	public synchronized boolean updateGamblerBalance(int gamblerId, int balance) {
		String query = "UPDATE Gambler " + 
					"SET balance = ? " + 
					"WHERE id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, balance);
					dbPrepStatement.setInt(2, gamblerId);
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Sets a given gambler's online status (using gambler name and password).
	 * @param gamblerName the gambler to update.
	 * @param gamblerPassword the gambler's password
	 * @param online the new online status.
	 * @return whether the update succeeded.
	 * @exception Exception
	 */
	public synchronized boolean updateGamblerOnline(String gamblerName, String gamblerPassword, boolean online) {
		String query = "UPDATE Gambler " + 
					"SET isOnline = ? " + 
					"WHERE name = ? " + 
					"AND password = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setBoolean(1, online);
					dbPrepStatement.setString(2, gamblerName);
					dbPrepStatement.setString(3, gamblerPassword);
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Updates all gamblers and sets their online status to 0 (offline).
	 * Used by the main server when it shuts down.
	 * @return
	 */
	public synchronized void updateGamblersOffline() {
		String query = "UPDATE Gambler " + 
					"SET isOnline = 0 ";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					dbStatement.executeUpdate(query);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates all ready and running races to state 6 (failed state).
	 * Used by the main server when it shuts down before races finished.
	 */
	public synchronized void updateRacesStateFinished() {
		String query = "UPDATE Race " + 
					"SET Race.state = 6 "+
					"WHERE Race.state BETWEEN 0 AND 4";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					dbStatement.executeUpdate(query);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finds out of a given car already exists in database (checks name).
	 * @param carName the car name to search.
	 * @return whether the car already exists.
	 * @exception Exception
	 */
	public boolean carExists(String carName) {
		String query = "SELECT Car.name " + 
					"FROM Car " + 
					"WHERE Car.name = '" + carName + "' ";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							if (resultSet.getString(1).equals(carName))
								return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Inserts a given new race to the database.
	 * @param race the new race to insert.
	 * @return whether the insertion succeeded.
	 * @exception Exception
	 */
	public synchronized boolean insertNewRace(Race race) {
		String query = "INSERT INTO Race " + 
					"(number, raceDate, state, totalBets, systemRevenue) " + 
					"VALUES ( ?, ?, ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, race.getNumber());
					dbPrepStatement.setDate(2, race.getDate());
					dbPrepStatement.setInt(3, race.getState());
					dbPrepStatement.setInt(4, race.getTotalBets());
					dbPrepStatement.setInt(5, 0);
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Updates a race's revenue.
	 * @param raceNumber the race to update.
	 * @param revenue the revenue to update.
	 * @return whether the update succeeded.
	 * @exception Exception
	 */
	public synchronized boolean setSystemRaceRevenue(int raceNumber, int revenue) {
		String query = "UPDATE Race " + 
					"SET Race.systemRevenue = " + revenue + " " + 
					"WHERE Race.number = " + raceNumber;
					
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					dbStatement.executeUpdate(query);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Inserts a given new gambler to the database.
	 * @param gambler the new gambler to insert.
	 * @return whether the insertion succeeded.
	 * @exception Exception.
	 */
	public synchronized boolean insertNewGambler(Gambler gambler) {
		String query = "INSERT INTO Gambler " + 
					"(id, name, password, balance) " + 
					"VALUES ( ?, ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, gambler.getId());
					dbPrepStatement.setString(2, gambler.getName());
					dbPrepStatement.setString(3, gambler.getPassword());
					dbPrepStatement.setInt(4, gambler.getBalance());
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if a given gambler already bet on a given car in a given race.
	 * @param raceNumber the race number.
	 * @param gamblerId the gambler that bets.
	 * @param carName the car that the gambler bet on.
	 * @return whether the gambler already bet on the give car in the given race.
	 */
	private int checkCarBetExists(int raceNumber, int gamblerId, String carName) {
		String query = "SELECT GamblerCarRace.bet " + 
				"FROM GamblerCarRace " + 
				"WHERE GamblerCarRace.carName = '" + carName + "' " +
				"AND GamblerCarRace.raceNumber = " + raceNumber + " " + 
				"AND GamblerCarRace.gamblerId = " + gamblerId;
	try {
		Class.forName("com.mysql.jdbc.Driver");
		try (Connection dbConnection = DriverManager
				.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
			try (Statement dbStatement = dbConnection.createStatement()) {
				try (ResultSet resultSet = dbStatement.executeQuery(query)) {
					if (resultSet.next())
						return resultSet.getInt(1);
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return -1;
	}

	/**
	 * Returns a HashMap that consists of all gambler bets.
	 * @param raceNumber the race number to get the bets from.
	 * @return
	 */
	public ArrayList<GamblerCarRace> getRaceBets(int raceNumber) {
		String query = "SELECT GamblerCarRace.gamblerId, GamblerCarRace.raceNumber, GamblerCarRace.carName, GamblerCarRace.bet " + 
					"FROM GamblerCarRace " + 
					"WHERE GamblerCarRace.raceNumber = " + raceNumber;
		ArrayList<GamblerCarRace> resultList = new ArrayList<>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/javarace?useSSL=false",
					"scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						while (resultSet.next())
							resultList.add(new GamblerCarRace(resultSet.getInt(1), resultSet.getInt(2),
									resultSet.getString(3), resultSet.getInt(4)));
						return resultList;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}