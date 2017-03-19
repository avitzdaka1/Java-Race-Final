package Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import Entities.GamblerCarRace;
import Entities.Race;
import Gambler.GamblerClient;
import Race.RaceController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CarRaceServer extends Application {
	
	public static int numOfConnections = 0;
	private Database database;
	private final int totalNumOfRaces = 3;
	private ArrayList<RaceHandlerListener> raceHandList;
	private ArrayList<GamblerHandlerListener> gambleHandlersList;
	private ArrayList<MainServerListener> clientHandlersArray;
	private int raceCounter = 0; // = race Number
	private boolean raceRunning = false;
	private CarLog carLog;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private TableView<ObservableList> tableView = new TableView<>();
	private ConcurrentHashMap<Integer, HashSet<String>> waitingRaces = new ConcurrentHashMap<>();
	
	public void start(Stage primaryStage) {
		createServerGUI(primaryStage);
	}

	/**
	 * Creates the main server gui.
	 * @param primaryStage
	 */
	public void createServerGUI(Stage primaryStage) {

		BorderPane pane = new BorderPane();
		VBox buttonsBoxVB = new VBox(), serverLogVB = new VBox();
		buttonsBoxVB.setPrefSize(160, 50);

		Button  btnNewGambler = new Button("New Gambler"), btnShowGamlers = new Button("Gamblers"), 
				btnHistory = new Button("History"), btnCurrentState = new Button("Current State"), 
				btnStatistics = new Button("Statistics"), btnClearLog = new Button("Clear Log");

		carLog = new CarLog();
		ScrollPane srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		srcPane.setContent(carLog);
		
		buttonsBoxVB.getChildren().addAll( btnNewGambler, btnShowGamlers, btnHistory, 
				btnCurrentState, btnStatistics, btnClearLog);
		serverLogVB.getChildren().addAll(new Label("Server LOG:"), srcPane);
		serverLogVB.setAlignment(Pos.CENTER);

		pane.setLeft(buttonsBoxVB);
		pane.setBottom(serverLogVB);
		pane.setCenter(tableView);

		Scene scene = new Scene(pane, 900, buttonsBoxVB.getMinHeight());
		scene.getStylesheets().add("Server/serverStyles.css");
		
	//	tableView.setPrefSize(primaryStage.getWidth(), 260);
		tableView.getStyleClass().add("table-view");
	//	tableView.setId("my-table");
		
		database = new Database();
		raceCounter = database.getLastRaceNumber();

		primaryStage.setScene(scene);
		primaryStage.setTitle("CarRace Server");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.show();
		
		//	Creates a new gambler window.
		btnShowGamlers.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				database.getAllGamblers(tableView);
				
			//	tableView.getColumns().get(0).setVisible(true);
		//		populateTableView(database.getAllGamblers(), tableView);
			}
		});
		
		//	Creates a new gambler window.
		btnNewGambler.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				carLog.printMsg("New gambler started " + dateFormat.format(new Date()));
				startNewGambler();
			}
		});
		
		//	Clears the server log.
		btnClearLog.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.runLater(() -> {
					carLog.clearLog();
					carLog.printMsg("The Log was cleared at " + dateFormat.format(new Date()) + "\n\n");
				});
			}
		});
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				try {
					//	Notify all handlers to close their windows and disconnect clients.
					 for(MainServerListener listener : clientHandlersArray )
						 listener.serverDisconnection();	
					 database.updateGamblersOffline();
					 database.updateRacesStateFinished();
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					Platform.exit();
					System.exit(0);
				}
			}
		});

		carLog.printMsg("Server was started at " + dateFormat.format(new Date()));
		
		raceHandList = new ArrayList<RaceHandlerListener>();
		clientHandlersArray = new ArrayList<MainServerListener>();
		gambleHandlersList = new ArrayList<GamblerHandlerListener>();
		
		listenNewGambler();
		listenNewRace();
		openRaces();
	}

	/**
	 * Starts a new gambler client thread.
	 */
	public void startNewGambler() {
		GamblerClient gamblerClient = new GamblerClient();
		Thread thread = new Thread(gamblerClient);
		thread.start();
	}
	/**
	 * Starts 3 races when the program first starts up.
	 */
	public void openRaces() {
		for(int i = 0; i < totalNumOfRaces; i++) {
			RaceController raceController = new RaceController();
			Thread thread = new Thread(raceController);
			thread.start();
		}
	}

	/**
	 * Listens for new race connections (from race controllers) and allocates a handler for each one.
	 * @exception IOException
	 */
	public void listenNewRace() {

		new Thread(() -> {
			try (ServerSocket serverSocket = new ServerSocket(8888)) {	//	TODO: Create final for port number.
				while (true) {
					Socket clientSocket = serverSocket.accept();
					InetAddress clientAddress = clientSocket.getInetAddress();
					Platform.runLater(()-> {
						carLog.printMsg("New race connected from " + clientAddress.getHostAddress() + " at " + dateFormat.format(new Date()));
					});
					HandlerRace handlerRace = new HandlerRace(clientSocket, this, ++raceCounter,
							database, carLog);
					Race race = new Race(raceCounter, new java.sql.Date(new Date().getTime()));
					waitingRaces.put(raceCounter, new HashSet<String>());
					database.insertNewRace(race);
					clientHandlersArray.add(handlerRace);
					raceHandList.add(handlerRace);
					Thread thread = new Thread(handlerRace);
					thread.start();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * Listens for new gambler connections (from gambler clients) and allocates a handler of each one.
	 * @exception IOException
	 */
	public void listenNewGambler() {

		new Thread(() -> {
			try (ServerSocket serverSocket = new ServerSocket(8889)) {	//	TODO: Create final for port number.
				while (true) {
					Socket clientSocket = serverSocket.accept();
					InetAddress clientAddress = clientSocket.getInetAddress();
					Platform.runLater(()-> {
						carLog.printMsg("New gambler connected from " + clientAddress.getHostAddress() + " at " + dateFormat.format(new Date()));
					});
					HandlerGambler handlerGambler = new HandlerGambler(clientSocket, this, database);
					clientHandlersArray.add(handlerGambler);
					gambleHandlersList.add(handlerGambler);
					Thread thread = new Thread(handlerGambler);
					thread.start();		
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * Updates all connected gamblers with the current races that can be bet on.
	 */
	public void updateGamblersRaces() {
		for (int i = 0; i < gambleHandlersList.size(); i++) 
			gambleHandlersList.get(i).updateRaces();	
	}

	/**
	 * Returns the HashMap of races that are now waiting to start.
	 * @return the HashMap of races that are now waiting to start.
	 */
	public ConcurrentHashMap<Integer, HashSet<String>> getWaitingRaces() {
		return waitingRaces;
	}

	/**
	 * Starts a new race (if a new race can start).
	 */
	public void startNewRace() {
		int chosenRace = -1, maxBets = 0;
		//	If a race is not running already.
		if (!raceRunning) {
			//	Find a race that is ready and that has the highest amount of total bets.
			for(Integer raceNumber : waitingRaces.keySet()) {
				if (waitingRaces.get(raceNumber).size() == 3 && database.getRaceTotalBets(raceNumber) > maxBets)
					chosenRace = raceNumber;
			}
			//	If a race was found (-1 means there isn't a race ready).
			if (chosenRace != -1) {
				//	Notify the relevant race handler to start the race.
				for(RaceHandlerListener listener : raceHandList) {
					if (((HandlerRace)listener).getRaceNumber() == chosenRace) {
						listener.startRace();
						raceRunning = true;
					}
				}
			}
		}
		
	}
	
	/**
	 * Checks if a race is currently running.
	 * @return whether a race is currently running.
	 */
	public boolean isRaceRunning() {
		return raceRunning;
	}

	/**
	 * Opens a new race window (controller).
	 */
	public void openNewRace() {
		this.raceRunning = false;
		RaceController raceController = new RaceController();
		Thread thread = new Thread(raceController);
		thread.start();
	}

	public static void main(String[] args) {
		launch(args);
	}


}