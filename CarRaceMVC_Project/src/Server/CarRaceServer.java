package Server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Entities.MessageGambler;
import Gambler.GamblerClient;
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
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CarRaceServer extends Application {

	public static int numOfConnections = 0;
	public Database database;
	private ArrayList<Model> modelList;
	private ArrayList<MainServerListener> ClientHandlersArray;
	private int raceCounter = 0; // = race Number
	private int gamblerCounter = 0; // = gambler Number
	private CarLog carLog;
	private ServerSocket serverSocket;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private Date date = new Date();


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
				carLog.printMsg("New gambler started " + dateFormat.format(date));
				startNewGambler();
			}
		});

		//	Clears the server log.
		btnClearLog.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.runLater(() -> {
					carLog.clearLog();
					carLog.printMsg("The Log was cleared at " + dateFormat.format(date) + "\n\n");
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
					 for(MainServerListener listener : ClientHandlersArray )
						 listener.serverDisconnection();					 
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					Platform.exit();
					System.exit(0);
				}
			}
		});

		carLog.printMsg("Server was started at " + dateFormat.format(date));
		
		modelList = new ArrayList<Model>();
		ClientHandlersArray = new ArrayList<MainServerListener>();
		
		listenNewGambler();
		listenNewRace();		
	}

	// Open new thread for new client.
	public void startNewGambler() {
		GamblerClient gamblerClient = new GamblerClient();
		Thread thread = new Thread(gamblerClient);
		thread.start();
	}

	public void listenNewRace() {

		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(8888);	//	TODO: Create final for port number.
				while (true) {
					Socket clientSocket = serverSocket.accept();
					InetAddress clientAddress = clientSocket.getInetAddress();
					
					Platform.runLater(()-> {
						carLog.printMsg("New race connected from " + clientAddress.getHostAddress() + " at " + dateFormat.format(date));
						
					});
					HandlerRace handlerRace = new HandlerRace(clientSocket, this, ++raceCounter, database);
					ClientHandlersArray.add(handlerRace);
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
				serverSocket = new ServerSocket(8889);	//	TODO: Create final for port number.
				while (true) {
					Socket clientSocket = serverSocket.accept();
					InetAddress clientAddress = clientSocket.getInetAddress();
					
					Platform.runLater(()-> {
						carLog.printMsg("New gambler connected from " + clientAddress.getHostAddress() + " at " + dateFormat.format(date));
						
					});
					
					gamblerCounter++;
					HandlerGambler handlerGambler = new HandlerGambler(clientSocket, this, ++gamblerCounter, database);
					ClientHandlersArray.add(handlerGambler);
					Thread thread = new Thread(handlerGambler);
					thread.start();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
		
		
	
	
	
	
	// Client Handler
	/*public void HandleAClient(Socket clientSocket) {

		new Thread(() -> {
			try {
				ObjectInputStream inputStreamFromClient = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream outputStreamToClient = new ObjectOutputStream(clientSocket.getOutputStream());

				while (true) {
					CarEvents.eventType event = (CarEvents.eventType) inputStreamFromClient.readObject();
					Object obj = inputStreamFromClient.readObject();
					
				//	runFunctionOnServer(outputStreamToClient, event, obj);
					//if (event == CarEvents.eventType.Disconnect) {
					//	break;
					//}
				}
			} catch (SocketException ex) {
				try {
					clientSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}*/

	
	public synchronized void runFunctionOnServer(MessageGambler message) throws IOException {
		switch (1) {

		/*case Connect:
			modelList.add(new Model(++raceCounter));
			outputStreamToClient.writeObject(CarEvents.eventType.Connect);
			outputStreamToClient.writeObject(raceCounter);
			carLog.printMsg("Client #" + raceCounter + " from: " + clientAddress.getHostAddress() + " connected");
			break;

		case Disconnect: //Close connection with client
			carLog.printMsg("Client #" + log.getRaceNum() + " disconnected!");
			break;

		case COLOR:
			try { // Converting from string to COLOR - unnecessary.
				Field f = Color.class.getField(colorType[log.colorIndex]);
				Color c = (Color) f.get(null);
				modelList.get(log.getRaceNum()-1).changeColor(log.getCarNum(), c);////////////////////////
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			carLog.printMsg("In Client #" + log.getRaceNum() + " Color of Car #" + log.getCarNum() + " was changed to: " + colorType[log.colorIndex]);
			break;

		case SPEED:
			modelList.get(log.getRaceNum()-1).changeSpeed(log.getCarNum(), log.getSpeed());
			carLog.printMsg("In Client #" + log.getRaceNum() + " Speed of Car #" + log.getCarNum() + " was changed to: " + log.getSpeed());
			break;

		case RADIUS:
			modelList.get(log.getRaceNum()-1).changeRadius(log.getCarNum(), log.getRadius());
			carLog.printMsg("In Client #" + log.getRaceNum() + " Radius of Car #" + log.getCarNum() + " was changed to: " + log.getRadius());
			break;
		default:
			break;*/
		}
	}

	public static void main(String[] args) {
		launch(args);
	}


}