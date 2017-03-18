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
	private ArrayList<HandlerRace> modelList;
	private ArrayList<GamblerHandlerListener> gambleHandlersList;
	private ArrayList<MainServerListener> clientHandlersArray;
	private int raceCounter = 0; // = race Number
	private CarLog carLog;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private TableView<ObservableList> tableView = new TableView<>();

	public void start(Stage primaryStage) {
		createServerGUI(primaryStage);
	}

	public void createServerGUI(Stage primaryStage) {

		BorderPane pane = new BorderPane();
		VBox buttonsBoxVB = new VBox(), serverLogVB = new VBox();
		buttonsBoxVB.setPrefSize(160, 50);

		tableView.setPrefSize(primaryStage.getWidth(), 260);

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
		
		modelList = new ArrayList<HandlerRace>();
		clientHandlersArray = new ArrayList<MainServerListener>();
		gambleHandlersList = new ArrayList<GamblerHandlerListener>();
		
		listenNewGambler();
		listenNewRace();
		startRaces();
	}

	// Open new thread for new client.
	public void startNewGambler() {
		GamblerClient gamblerClient = new GamblerClient();
		Thread thread = new Thread(gamblerClient);
		thread.start();
	}
	/**
	 * Starts 3 races when the program first starts up.
	 */
	public void startRaces() {
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
					database.insertNewRace(race);
					clientHandlersArray.add(handlerRace);
					modelList.add(handlerRace);
					Thread thread = new Thread(handlerRace);
					thread.start();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
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
	
	public void updateGamblersRaces() {
		for (int i = 0; i < gambleHandlersList.size(); i++) 
			gambleHandlersList.get(i).updateRaces();	
	}

	public static void main(String[] args) {
		launch(args);
	}


}