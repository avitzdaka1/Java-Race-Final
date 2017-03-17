package Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Entities.MessageGambler;
import Gambler.GamblerClient;
import Race.RaceController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
	private ArrayList<MainServerListener> clientHandlersArray;
	private int raceCounter = 0; // = race Number
	private int gamblerCounter = 0; // = gambler Number
	private CarLog carLog;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public void start(Stage primaryStage) {
		createServerGUI(primaryStage);
	}

	public void createServerGUI(Stage primaryStage) {

		BorderPane pane = new BorderPane();
		VBox buttonsBoxVB = new VBox(), serverLogVB = new VBox();
		buttonsBoxVB.setPrefSize(160, 50);
		
		TableView<String> tableView = new TableView<>();
		tableView.setPrefSize(primaryStage.getWidth(), 260);

		Button  btnNewGambler = new Button("New Gambler"), btnEditUsers = new Button("Users"), 
				btnHistory = new Button("History"), btnCurrentState = new Button("Current State"), 
				btnStatistics = new Button("Statistics"), btnClearLog = new Button("Clear Log");

		carLog = new CarLog();
		ScrollPane srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		srcPane.setContent(carLog);
		
		buttonsBoxVB.getChildren().addAll( btnNewGambler, btnEditUsers, btnHistory, 
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
		gamblerCounter = database.getLastGamblerId();
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

		primaryStage.setScene(scene);
		primaryStage.setTitle("CarRace Server");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				try {
					//	Notify all handlers to close their windows amd disconnect clients.
					 for(MainServerListener listener : clientHandlersArray )
						 listener.serverDisconnection();					 
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
		
		listenNewGambler();
		listenNewRace();		
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
			try {
				ServerSocket serverSocket = new ServerSocket(8888);	//	TODO: Create final for port number.
				while (true) {
					Socket clientSocket = serverSocket.accept();
					InetAddress clientAddress = clientSocket.getInetAddress();
					
					Platform.runLater(()-> {
						carLog.printMsg("New race connected from " + clientAddress.getHostAddress() + " at " + dateFormat.format(new Date()));
						
					});
					HandlerRace handlerRace = new HandlerRace(clientSocket, this, ++raceCounter, database);
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
			try {
				ServerSocket serverSocket = new ServerSocket(8889);	//	TODO: Create final for port number.
				while (true) {
					
					Socket clientSocket = serverSocket.accept();
					InetAddress clientAddress = clientSocket.getInetAddress();
					Platform.runLater(()-> {
						carLog.printMsg("New gambler connected from " + clientAddress.getHostAddress() + " at " + dateFormat.format(new Date()));
					});
					HandlerGambler handlerGambler = new HandlerGambler(clientSocket, this, ++gamblerCounter, database);
					clientHandlersArray.add(handlerGambler);
					Thread thread = new Thread(handlerGambler);
					thread.start();		
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	public static void main(String[] args) {
		launch(args);
	}


}