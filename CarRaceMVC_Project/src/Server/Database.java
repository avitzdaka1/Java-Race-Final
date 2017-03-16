	package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import Entities.*;

public class Database {

	// DB connecting information
	private final String RACE_DB = "RACE_DB.txt";

	public Database() {
		if (!checkDBExists())
			createNewDB();
	}
	
	//	Checks if database tables exist (car table must always be not empty).
	private boolean checkDBExists() {
		String query = "SELECT * FROM Car";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/javarace", "scott",
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
	
	// create new DB if not exists , loads create commands from file
	public boolean createNewDB () {
		try (FileInputStream fstream = new FileInputStream(RACE_DB)) {
			try (DataInputStream in = new DataInputStream(fstream)) {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
					Class.forName("com.mysql.jdbc.Driver");
					try (Connection dbConnection = DriverManager
							.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Returns all race records.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getFinishedRaces() {
		String query = "SELECT Race.number, Race.raceDate, Race.totalBets " + 
					"FROM Race " + 
					"WHERE Race.state = 5" + // state finished
					"ORDER BY Race.number ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						// ArrayList<RaceResult> raceResults = new
						// ArrayList<>();
						while (resultSet.next()) {
							// RaceResult result = new
							// RaceResult(resultSet.getInt(1),
							// resultSet.getDate(2), resultSet.getInt(3));
							// raceResults.add(result);
						}
						// return raceResults;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Returns all gambler records.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getAllGamblers() {
		String query = "SELECT Gambler.id, Gambler.name, Gambler.password, Gambler.balance " + 
					"FROM Gambler " + 
					"ORDER BY Gambler.id ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						while (resultSet.next()) {
							// GamblerResult result = new
							// GamblerResult(resultSet.getInt(1),
							// resultSet.getString(2), resultSet.getString(3),
							// resultSet.getInt(4));
							// gamblerResults.add(result);
						}
						// return raceResults;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Returns race results (cars names and positions, gamblers and their
	// bets+revenues), given the race number.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getFinishedRaceResults(int raceNumber) {
		String query = "SELECT CarRaceResult.position, CarRaceResult.carName, GamblerCarRace.gamblerId, GamblerCarRace.bet, GamblerRaceResult.revenue " + 
					"FROM CarRaceResult, GamblerCarRace, GamblerRaceResult, Race " + 
					"WHERE CarRaceResult.raceNumber = " + raceNumber + " " + 
					"AND CarRaceResult.raceNumber = Race.number " + 
					"AND Race.number = GamblerCarRace.raceNumber " + 
					"AND GamblerRaceResult.raceNumber = Race.number " + 
					"AND Race.state = 5 " + "ORDER BY CarRaceResult.position ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						// ArrayList<CarRaceResult> carRaceResults = new
						// ArrayList<>();
						while (resultSet.next()) {
							// CarRaceResult result = new
							// CarRaceResult(resultSet.getInt(1),
							// resultSet.getString(2));
							// carRaceResults.add(result);
						}
					}
				}
			} // return carRaceResults;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Returns gambler race results (all races, bets, cars bet on, revenues),
	// given the gambler id number.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getGamblerHistory(int gamblerId) {
		String query = "SELECT GamblerCarRace.raceNumber, Gambler.name, GamblerCarRace.carName, GamblerCarRace.bet, GamblerRaceResult.revenue " + 
					"FROM GamblerCarRace, Gambler, GamblerRaceResult " + 
					"WHERE Gambler.id = " + gamblerId + " " + 
					"AND Gambler.id = GamblerCarRace.gamblerId " + 
					"AND Gambler.id = GamblerRaceResult.gamblerId " + 
					"ORDER BY GamblerCarRace.raceNumber ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						// ArrayList<CarRaceResult> carRaceResults = new
						// ArrayList<>();
						while (resultSet.next()) {
							// CarRaceResult result = new
							// CarRaceResult(resultSet.getInt(1),
							// resultSet.getString(2));
							// carRaceResults.add(result);
						}
					}
				}
			} // return carRaceResults;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Returns live race results (cars names, gamblers and their bets), given
	// the race number.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getLiveRaceResults(int raceNumber) {
		String query = "SELECT Race.number, Race.state, GamblerCarRace.gamblerId, CarRaceResult.carName,  GamblerCarRace.bet " + 
					"FROM CarRaceResult, GamblerCarRace, Race " + 
					"WHERE CarRaceResult.raceNumber = " + raceNumber + " " + 
					"AND CarRaceResult.raceNumber = Race.number " + 
					"AND Race.number = GamblerCarRace.raceNumber " + 
					"ORDER BY Race.number ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						// ArrayList<CarRaceResult> carRaceResults = new
						// ArrayList<>();
						while (resultSet.next()) {
							// CarRaceResult result = new
							// CarRaceResult(resultSet.getInt(1),
							// resultSet.getString(2));
							// carRaceResults.add(result);
						}
						// return carRaceResults;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets the last race number.
	public int getLastRaceNumber() {
		String query = "SELECT Race.number " + 
					"FROM Race " + 
					"ORDER BY Race.number DESC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Gets the last gambler id.
	public int getLastGamblerId() {
		String query = "SELECT Gambler.id " + 
					"FROM Gambler " + 
					"ORDER BY Gambler.id DESC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Gets gambler details using given gambler name.
	public Gambler getGamblerDetails(String gamblerName) {
		String query = "SELECT * " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "'";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Finds out of a given gambler already exists in database (checks name).
	public boolean gamblerExists(String gamblerName) {
		String query = "SELECT Gambler.name " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "'";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Finds out if a given gambler (name and password) is online.
	public boolean gamblerOnline(String gamblerName, String gamblerPassword) {
		String query = "SELECT Gambler.name, Gambler.password, Gambler.isOnline " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "' " + 
					"AND Gambler.password = '" + gamblerPassword + "'";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next())
							if (resultSet.getBoolean(3))
								return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Checks a given gambler's login credentials, and if the gambler is
	// currently online.
	public boolean checkGamblerAuth(String gamblerName, String gamblerPassword) {
		String query = "SELECT Gambler.name, Gambler.password, Gambler.isOnline " + 
					"FROM Gambler " + 
					"WHERE Gambler.name = '" + gamblerName + "' " + 
					"AND Gambler.password = '" + gamblerPassword + "'";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Checks if gambler has enough balance to afford given bet,
	// if he does, bet on given car in a given race.
	public boolean gamblerBet(Gambler gambler, Race race, Car car, int bet) {
		String query = "SELECT Gambler.balance " + 
					"FROM Gambler " +
					"WHERE Gambler.name = '" + gambler.getName() + "' ";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						if (resultSet.next()) {
							int balance = resultSet.getInt(1);
							// If gambler's balance is equal or greater than bet.
							if (bet <= balance) {
								// Place gambler bet.
								placeGamblerBet(gambler.getId(), race.getNumber(), car.getName(), bet);
								updateRaceTotalBets(race.getNumber(), bet);
								updateGamblerBalance(gambler, balance - bet);
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

	// Places a gambler bet on a given car and race.
	public synchronized boolean placeGamblerBet(int gamblerId, int raceNumber, String carName, int bet) {
		String query = "INSERT INTO GamblerCarRace " + 
					"(gamblerId, raceNumber, carName, bet) " + 
					"VALUES ( ?, ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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
	
	// Inserts new gambler-race result (revenue from race).
	public synchronized boolean insertGamblerRaceResult(int gamblerId, int raceNumber, int revenue) {
		String query = "INSERT INTO GamblerRaceResult " + 
					"(gamblerId, raceNumber, revenue) " + 
					"VALUES ( ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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
	
	// Inserts new car-race result (car's position in race).
		public synchronized boolean insertCarRaceResult(int raceNumber, String carName, int position) {
			String query = "INSERT INTO CarRaceResult " + 
						"(raceNumber, carName, position) " + 
						"VALUES ( ?, ?, ?)";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				try (Connection dbConnection = DriverManager
						.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Updates a given race's total bets.
	public synchronized boolean updateRaceTotalBets(int raceNumber, int bet) {
		String query = "UPDATE Race " + 
					"SET totalBets = ? " + 
					"WHERE number = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					int raceTotalBets = getRaceTotalBets(raceNumber);
					if (raceTotalBets != -1) {
						dbPrepStatement.setInt(1, raceTotalBets + bet);
						dbPrepStatement.setInt(2, raceNumber);
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
		
	// Retrieves a given race's total bets (using race number).
	public int getRaceTotalBets(int raceNumber) {
		String query = "SELECT Race.totalBets " + 
					"FROM Race " + 
					"WHERE Race.number = " + raceNumber;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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
		
	// Updates a given gambler's balance.
	public synchronized boolean updateGamblerBalance(Gambler gambler, int balance) {
		String query = "UPDATE Gambler " + 
					"SET balance = ? " + 
					"WHERE id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, balance);
					dbPrepStatement.setString(2, gambler.getName());
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Sets a given gambler's online status (using gambler name and password).
	public synchronized boolean updateGamblerOnline(String gamblerName, String gamblerPassword, boolean online) {
		String query = "UPDATE Gambler " + 
					"SET isOnline = ? " + 
					"WHERE name = ? " + 
					"AND password = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Finds out of a given car already exists in database (checks name).
	public boolean carExists(String carName) {
		String query = "SELECT Car.name " + 
					"FROM Car " + 
					"WHERE Car.name = '" + carName + "' ";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

	// Inserts a given new race to the database.
	public synchronized boolean insertNewRace(Race race) {
		String query = "INSERT INTO Race " + 
					"(number, raceDate, state, totalBets) " + 
					"VALUES ( ?, ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
				try (PreparedStatement dbPrepStatement = dbConnection.prepareStatement(query)) {
					dbPrepStatement.setInt(1, race.getNumber());
					dbPrepStatement.setDate(2, race.getDate());
					dbPrepStatement.setInt(3, race.getState());
					dbPrepStatement.setInt(4, race.getTotalBets());
					dbPrepStatement.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Inserts a given new gambler to the database.
	public synchronized boolean insertNewGambler(Gambler gambler) {
		String query = "INSERT INTO Gambler " + 
					"(id, name, password, balance) " + 
					"VALUES ( ?, ?, ?, ?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace", "scott", "tiger")) {
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

/*
	// Inserts a given new car to the database. public boolean
	public synchronized boolean insertNewCar(ServerCar car) {
		String query = "INSERT INTO Car " + 
					"(name, make, size, color, type) " + 
					"VALUES ( ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = dbConnection.prepareStatement(query);
			statement.setString(1, car.getName());
			statement.setString(2, car.getMake());
			statement.setString(3, car.getSize());
			statement.setString(4, car.getColor());
			statement.setString(5, car.getType());
			statement.executeUpdate();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			return false;
		}
		return true;
	}
	*/
}