package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Database {

	// DB connecting information
	private String dbAdress, dbName, dbUserName, dbPassword;
	private final String RACE_DB = "RACE_DB.sql";

	// Statement for executing queries
	private Statement dbStateMent;

	public Database(String dbAdress, String dbName, String dbUserName, String dbPassword) {
		this.dbAdress = dbAdress;
		this.dbName = dbName;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;

		initializeDB();

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(RACE_DB);
			createDB(fstream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// create new DB if not exists , loads create commands from file
	private void createDB(FileInputStream fstream) {

		try {
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = "", strLine1 = "";
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine != null && !strLine.trim().equals("")) {
					if ((strLine.trim().indexOf("/*") < 0) && (strLine.trim().indexOf("*/") < 0)) {
						if (strLine.indexOf(';') >= 0) {
							strLine1 += strLine;
							System.out.println(strLine1);
							dbStateMent.execute(strLine1);
							strLine1 = "";
						} else
							strLine1 += strLine;
					}
				}
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public int update(String query) {
		try {
			if (!query.isEmpty() && query != null)
				return dbStateMent.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ResultSet executeQuery(String query) {
		try {
			if (!query.isEmpty() && query != null)
				return dbStateMent.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void initializeDB() {
		try {
			// Load the JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			// Establish a connection
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + dbAdress + "/" + dbName, dbUserName,
					dbPassword);
			// Create a statement
			dbStateMent = connection.createStatement();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Returns all race records.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getAllRaces() {
		String query = "SELECT Race.number, Race.raceDate, Race.totalBets " + 
					"FROM Race " +
					"WHERE Race.state = 5" +	//	state finished
					"ORDER BY Race.number DSC";
		ResultSet resultSet = executeQuery(query);
		// ArrayList<RaceResult> raceResults = new ArrayList<>();
		try {
			while (resultSet.next()) {
				//	RaceResult result = new RaceResult(resultSet.getInt(1), resultSet.getDate(2), resultSet.getInt(3));
				//	raceResults.add(result);
			}
			//	return raceResults;
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
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
	ResultSet resultSet = executeQuery(query);
	// ArrayList<GamblerResult> gamblerResults = new ArrayList<>();
	try {
		while (resultSet.next()) {
			//	GamblerResult result = new GamblerResult(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4));
			//	gamblerResults.add(result);
		}
		//	return raceResults;
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}

	// Returns car-race results (cars and their final position in race), given its number.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getCarRaceResult(int raceNumber) {
		String query = "SELECT CarRaceResult.position, CarRaceResult.carName " + 
				"FROM CarRaceResult " +
				"WHERE CarRaceResult.raceNumber = " + raceNumber + " " +
				"ORDER BY CarRaceResult.position ASC";
	ResultSet resultSet = executeQuery(query);
	// ArrayList<CarRaceResult> carRaceResults = new ArrayList<>();
	try {
		while (resultSet.next()) {
			//	CarRaceResult result = new CarRaceResult(resultSet.getInt(1), resultSet.getString(2));
			//	carRaceResults.add(result);
		}
		//	return carRaceResults;
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}

	// Returns gambler-race results (cars and their final position in race), given its number.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getCarRaceResult(int raceNumber) {
		String query = "SELECT CarRaceResult.position, CarRaceResult.carName " + 
				"FROM CarRaceResult " +
				"WHERE CarRaceResult.raceNumber = " + raceNumber + " " +
				"ORDER BY CarRaceResult.position ASC";
	ResultSet resultSet = executeQuery(query);
	// ArrayList<CarRaceResult> carRaceResults = new ArrayList<>();
	try {
		while (resultSet.next()) {
			//	CarRaceResult result = new CarRaceResult(resultSet.getInt(1), resultSet.getString(2));
			//	carRaceResults.add(result);
		}
		//	return carRaceResults;
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}
	
	// Returns gambler details, given his id number.
	// TODO: Create some sort of a race result to return to the function caller
	// and to display
	// in a his tableview.
	public void getGambler(int gamblerId) {

	}

}
