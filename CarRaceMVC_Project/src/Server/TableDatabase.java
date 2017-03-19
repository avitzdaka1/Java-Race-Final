package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class TableDatabase {

	/**
	 * Returns all gambler records.
	 * TODO: update @param and @return after changing this method.
	 */
	public static void getAllGamblers(TableView tableView) {
		String query = "SELECT Gambler.id, Gambler.name, Gambler.password, Gambler.balance " + 
					"FROM Gambler " + 
					"ORDER BY Gambler.id ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						populateTableView(resultSet, tableView);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns all gambler records.
	 * TODO: update @param and @return after changing this method.
	 */
	public static void getAllRaces(TableView tableView) {
		String query = "SELECT Race.number, Race.raceDate, Race.state, Race.totalBets " + 
					"FROM Race " + 
					"ORDER BY Race.number ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						populateTableView(resultSet, tableView);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns gambler race bets (all races, bets, cars bet on),
	 * given the gamb'ers id number.
	 * @param gamblerId the gambler's id number.
	 * TODO: update @param and @return after changing this method.
	 * @exception Exception
	 */
	public static void getGamblerBets(TableView tableView, int gamblerId) {
		String query = "SELECT GamblerCarRace.raceNumber, Gambler.name, GamblerCarRace.carName, GamblerCarRace.bet " + 
					"FROM GamblerCarRace, Gambler " + 
					"WHERE Gambler.id = " + gamblerId + " " + 
					"AND Gambler.id = GamblerCarRace.gamblerId " + 
					"ORDER BY GamblerCarRace.raceNumber ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						populateTableView(resultSet, tableView);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns gambler race bets (all races, bets, cars bet on),
	 * given the gamb'ers id number.
	 * @param gamblerId the gambler's id number.
	 * TODO: update @param and @return after changing this method.
	 * @exception Exception
	 */
	public static void getGamblerRevenues(TableView tableView, int gamblerId) {
		String query ="SELECT CarRaceResult.gamblerId, CarRaceResult.raceNumber, GamblerRaceResult.revenue " + 
				"FROM GamblerRaceResult, Gambler " + 
				"WHERE CarRaceResult.gamblerId = " + gamblerId + " " + 
				"AND Gambler.id = CarRaceResult.gamblerId " ;  
			//	"AND Race.state = 5 " + "ORDER BY CarRaceResult.position ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
						populateTableView(resultSet, tableView);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns race results (cars names and positions, gamblers and their bets + revenues),
	 * given the race number.
	 * @param raceNumber the race number you wish to search for.
	 * TODO: update @param and @return after changing this method.
	 * @exception Exception
	 */
	public static void getFinishedRaceResults(int raceNumber) {
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
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
				try (Statement dbStatement = dbConnection.createStatement()) {
					try (ResultSet resultSet = dbStatement.executeQuery(query)) {
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
	
	
	/**
	 * Returns live race results (car names, gamblers and their bets), given the race number.
	 * TODO: update @param and @return after changing this method.
	 * @param raceNumber the race number to search for.
	 * @exception Exception
	 */
	public static void getLiveRaceResults(int raceNumber) {
		String query = "SELECT Race.number, Race.state, GamblerCarRace.gamblerId, CarRaceResult.carName,  GamblerCarRace.bet " + 
					"FROM CarRaceResult, GamblerCarRace, Race " + 
					"WHERE CarRaceResult.raceNumber = " + raceNumber + " " + 
					"AND CarRaceResult.raceNumber = Race.number " + 
					"AND Race.number = GamblerCarRace.raceNumber " + 
					"ORDER BY Race.number ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
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
	
	
	
	/**
	 * Returns all finished race records.
	 * TODO: update @param and @return after changing this method.
	 * @exception Exception
	 */
	public static void getFinishedRaces() {
		String query = "SELECT Race.number, Race.raceDate, Race.totalBets " + 
					"FROM Race " + 
					"WHERE Race.state >= 5 " + // state finished
					"ORDER BY Race.number ASC";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection dbConnection = DriverManager
					.getConnection("jdbc:mysql://localhost/javarace?useSSL=false", "scott", "tiger")) {
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
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void populateTableView (ResultSet rs, TableView<ObservableList> tableView) throws SQLException {
		tableView.getColumns().clear();
		ObservableList<ObservableList> data = FXCollections.observableArrayList();		
		
		for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            //We are using non property style for making dynamic table
            final int j = i;                
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                    return new SimpleStringProperty(param.getValue().get(j).toString());                        
                }                    
            });
            tableView.getColumns().addAll(col); 
        }

        /********************************
         * Data added to ObservableList *
         ********************************/
        while(rs.next()){           
            ObservableList<String> row = FXCollections.observableArrayList();  //Iterate Row
            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){            
                row.add(rs.getString(i)); //Iterate Column
            }
            data.add(row);
        }
        //FINALLY ADDED TO TableView
        tableView.setItems(data);
	}	
	
}
