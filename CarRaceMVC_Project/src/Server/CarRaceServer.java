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
	private ArrayList<Model> modelList;
	private int raceCounter = 0; // = clientNumber
	private static TextArea taLog;

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private InetAddress clientAddress;

	public static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	public static Date date = new Date();

	public void start(Stage primaryStage) {
		createServerGUI(primaryStage);
	}

	public void createServerGUI(Stage primaryStage) {

		BorderPane pane = new BorderPane();
		VBox buttonsBoxVB = new VBox(), serverLogVB = new VBox();
		buttonsBoxVB.setPrefSize(160, 50);
		
		taLog = new TextArea();
		taLog.setEditable(false);
		
		TableView<String> tableView = new TableView<>();
		tableView.setPrefSize(primaryStage.getWidth(), 260);

		Button  btnNewGambler = new Button("New Gambler"), btnEditUsers = new Button("Users"), 
				btnHistory = new Button("History"), btnCurrentState = new Button("Current State"), 
				btnStatistics = new Button("Statistics"), btnClearLog = new Button("Clear Log");

		ScrollPane srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		srcPane.setContent(taLog);
		
		buttonsBoxVB.getChildren().addAll( btnNewGambler, btnEditUsers, btnHistory, 
				btnCurrentState, btnStatistics, btnClearLog);
		serverLogVB.getChildren().addAll(new Label("Server LOG:"), srcPane);
		serverLogVB.setAlignment(Pos.CENTER);

		pane.setLeft(buttonsBoxVB);
		pane.setBottom(serverLogVB);
		pane.setCenter(tableView);

		Scene scene = new Scene(pane, 900, buttonsBoxVB.getMinHeight());
		scene.getStylesheets().add("Server/serverStyles.css");
		//primaryStage.getIcons().add(new Image(CarRaceServer.class.getResource("Server/resources/icon2.png").toExternalForm(),250,250,true,true));
		
		modelList = new ArrayList<Model>();

		btnNewGambler.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
			//	updateLog("BUTTON TEST " + dateFormat.format(date));
				startNewGambler();
			}
		});

		btnClearLog.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clearLog();
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
					Platform.exit();
					System.exit(0); ///
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		updateLog("Server was started at " + dateFormat.format(date));
		listenNewConnections();
	}

	// Open new thread for new client.
	public void startNewGambler() {
		new Thread(() -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
		//			new CarRaceClient(new Stage(), ++raceCounter);
				}
			});
		}).start();
	}

	public void listenNewConnections() {

		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(8888);	//	TODO: Create final for port number.
				while (true) {
					clientSocket = serverSocket.accept();
					clientAddress = clientSocket.getInetAddress();
					HandleAClient(clientSocket);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	// Client Handler
	public void HandleAClient(Socket clientSocket) {

		new Thread(() -> {
			try {
				ObjectInputStream inputStreamFromClient = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream outputStreamToClient = new ObjectOutputStream(clientSocket.getOutputStream());

				while (true) {
					CarEvents.eventType event = (CarEvents.eventType) inputStreamFromClient.readObject();
					Object obj = inputStreamFromClient.readObject();
					runFunctionOnServer(outputStreamToClient, event, obj);
					if (event == CarEvents.eventType.Disconnect) {
						break;
					}
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
	}

	
	public void runFunctionOnServer(ObjectOutputStream outputStreamToClient, CarEvents.eventType serverCommand, Object log) throws IOException {
		switch (serverCommand) {

		case Connect:
			modelList.add(new Model(++raceCounter));
			outputStreamToClient.writeObject(CarEvents.eventType.Connect);
			outputStreamToClient.writeObject(raceCounter);
			updateLog("Client #" + raceCounter + " from: " + clientAddress.getHostAddress() + " connected");
			break;

		case Disconnect: //Close connection with client
			updateLog("Client #" + log.getRaceNum() + " disconnected!");
			break;

		case COLOR:
			try { // Converting from string to COLOR - unnecessary.
				Field f = Color.class.getField(colorType[log.colorIndex]);
				Color c = (Color) f.get(null);
				modelList.get(log.getRaceNum()-1).changeColor(log.getCarNum(), c);////////////////////////
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			updateLog("In Client #" + log.getRaceNum() + " Color of Car #" + log.getCarNum() + " was changed to: " + colorType[log.colorIndex]);
			break;

		case SPEED:
			modelList.get(log.getRaceNum()-1).changeSpeed(log.getCarNum(), log.getSpeed());
			updateLog("In Client #" + log.getRaceNum() + " Speed of Car #" + log.getCarNum() + " was changed to: " + log.getSpeed());
			break;

		case RADIUS:
			modelList.get(log.getRaceNum()-1).changeRadius(log.getCarNum(), log.getRadius());
			updateLog("In Client #" + log.getRaceNum() + " Radius of Car #" + log.getCarNum() + " was changed to: " + log.getRadius());
			break;
		default:
			break;
		}
	}

	public static void updateLog(String str) {
		Platform.runLater(() -> {
			taLog.appendText(str + "\n-------------------------------------------\n");
		});
	}

	public static void clearLog() {
		Platform.runLater(() -> {
			taLog.setText("The Log was cleared at " + dateFormat.format(date) + "\n\n");
		});
	}

	public static void main(String[] args) {
		launch(args);
	}


}