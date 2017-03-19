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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
	private TableView<ObservableList> mainTableView = new TableView<>();
	private TableView<ObservableList> subTableView = new TableView<>();
	private ConcurrentHashMap<Integer, HashSet<String>> waitingRaces = new ConcurrentHashMap<>();
	private ComboBox<String> chooseRowCombo;
	private ObservableList<String> comboOptions = FXCollections.observableArrayList();
	private Label chooseLbl;
	private Button firstTablebutton, secondTableButton;
	
	public void start(Stage primaryStage) {
		createServerGUI(primaryStage);
	}

	/**
	 * Creates the main server gui.
	 * @param primaryStage
	 */
	public void createServerGUI(Stage primaryStage) {

		BorderPane pane = new BorderPane();
		pane.setStyle( "-fx-background-image: url(/Server/resources/serverBackground2.jpg);" );
		
		VBox buttonsBoxVB = new VBox(), serverLogVB = new VBox(), tablesVB = new VBox();

		primaryStage.getIcons().add(new Image(CarRaceServer.class.getResource("/Server/resources/icon2.png").toExternalForm(), 225,
				225, true, true));
		
		HBox tableControslHB = new HBox();
		tableControslHB.setSpacing(25);
		tableControslHB.setAlignment(Pos.CENTER);
		tableControslHB.setPadding(new Insets(3,10,3,10));
		
		Button  btnNewGambler = new Button("New Gambler"), btnShowGamlers = new Button("Gamblers"), 
				btnShowRaces = new Button("Races"), btnCurrentState = new Button("Current State"), 
				btnStatistics = new Button("Statistics"), btnClearLog = new Button("Clear Log");

		chooseRowCombo = new ComboBox<>();
		chooseRowCombo.setItems(comboOptions);
		firstTablebutton = new Button(); 
		secondTableButton = new Button();
		
		chooseRowCombo.setMinWidth(180);
		firstTablebutton.setId("buttonTable");
		secondTableButton.setId("buttonTable");
		
		carLog = new CarLog();
		ScrollPane srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		srcPane.setContent(carLog);
		
		buttonsBoxVB.getChildren().addAll( btnNewGambler, btnShowGamlers, btnShowRaces, 
				btnCurrentState, btnStatistics, btnClearLog);
		serverLogVB.getChildren().addAll(new Label("Server LOG:"), srcPane);
		serverLogVB.setAlignment(Pos.CENTER);

		mainTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		subTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		tableControslHB.getChildren().addAll(chooseLbl = new Label(), chooseRowCombo, firstTablebutton, secondTableButton);
		tablesVB.getChildren().addAll(mainTableView,tableControslHB,subTableView);
				
		pane.setLeft(buttonsBoxVB);
		pane.setBottom(serverLogVB);
		pane.setCenter(tablesVB);

		Scene scene = new Scene(pane, 1200, 800);
		scene.getStylesheets().add("Server/serverStyles.css");
		
		mainTableView.getStyleClass().add("table-view");
		
		database = new Database();
		raceCounter = database.getLastRaceNumber();

		primaryStage.setScene(scene);
		primaryStage.setTitle("CarRace Server");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.show();

		btnShowGamlers.setOnAction(eventShowGamblers);	
		btnShowRaces.setOnAction(eventShowRaces);
		
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
					// Notify all handlers to close their windows and disconnect
					// clients.
					for (MainServerListener listener : clientHandlersArray)
						listener.serverDisconnection();
					database.updateGamblersOffline();
					database.updateRacesStateFinished();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
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

	
	public final EventHandler<ActionEvent> eventShowGamblers = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Platform.runLater(() -> {
				TableDatabase.getAllGamblers(mainTableView);
				chooseLbl.setText("Gambler Id: ");
				firstTablebutton.setText("Bets");
				secondTableButton.setText("Revenues");
				comboOptions.clear();

				firstTablebutton.setOnAction(eventShowGamblerBets);
				secondTableButton.setOnAction(eventShowGamblerRevenues);

				TableColumn column = mainTableView.getColumns().get(0);
				for (ObservableList item : mainTableView.getItems())
					comboOptions.add((String) column.getCellObservableValue(item).getValue());
			});
		}
	};
	
	public final EventHandler<ActionEvent> eventShowRaces = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Platform.runLater(() -> {
				TableDatabase.getAllRaces(mainTableView);
				chooseLbl.setText("Race Id: ");
				firstTablebutton.setText("Race results");
				secondTableButton.setText("Gamblers and Bets");
				comboOptions.clear();
				
				firstTablebutton.setOnAction(eventShowRaceResults);
				secondTableButton.setOnAction(eventShowRaceGamblerBets);

				TableColumn column = mainTableView.getColumns().get(0);
				for (ObservableList item : mainTableView.getItems())
					comboOptions.add((String) column.getCellObservableValue(item).getValue());
			});
		}
	};
	
	public final EventHandler<ActionEvent> eventShowGamblerBets = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Platform.runLater(() -> {
			if(chooseRowCombo.getValue()!=null)
				TableDatabase.getGamblerBets(subTableView, Integer.parseInt(chooseRowCombo.getValue()));		
			});
		}
	};
	
	public final EventHandler<ActionEvent> eventShowGamblerRevenues = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Platform.runLater(() -> {
			if(chooseRowCombo.getValue()!=null)
				TableDatabase.getGamblerRevenues(subTableView, Integer.parseInt(chooseRowCombo.getValue()));
			});
		}
	};
	
	public final EventHandler<ActionEvent> eventShowRaceResults = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Platform.runLater(() -> {
				if (chooseRowCombo.getValue() != null) {
					// TODO:
				}	
			});
		}
	};

	public final EventHandler<ActionEvent> eventShowRaceGamblerBets = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Platform.runLater(() -> {
				if (chooseRowCombo.getValue() != null) {
					// TODO:
				}
			});
		}
	};
	
	public static void main(String[] args) {
		launch(args);
	}


}