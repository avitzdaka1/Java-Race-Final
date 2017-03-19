package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import Entities.Car;
import Entities.MessageRace;
import Entities.Race;
import Entities.RaceCommand;
import Race.RaceView;
import javafx.application.Platform;

public class HandlerRace implements Runnable, MainServerListener, RaceHandlerListener {

	private Socket clientSocket;
	private boolean raceConnected;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private int raceNumber, totalBets, systemRevenue;
	private CarRaceServer mainServer;
	private Database database;
	private CarLog carLog;
	private Car[] cars;
	private Race race;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private String winningCar;
	
	public HandlerRace(Socket clientSocket, CarRaceServer mainServer, int raceNumber, 
			Database database, CarLog carLog){
		this.clientSocket = clientSocket;
		this.mainServer = mainServer;
		this.raceNumber = raceNumber;
		this.database = database;
		this.carLog = carLog;
	}

	@Override
	public void run() {
		try {
			inputStream = new ObjectInputStream(clientSocket.getInputStream());
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			raceConnected = true;
			while (raceConnected) {
				MessageRace inputMessage = (MessageRace) inputStream.readObject();
				handleMessage(inputMessage);
			}
		} 		
		catch (SocketException e) {
			//e.printStackTrace();
		} 					
		catch (IOException e) {
			e.printStackTrace();
		} 		
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * Sends the race controller all available car names.
	 * @throws IOException
	 */
	private void sendCarNames() throws IOException {
		String[] carNames = database.getCarNames();
		MessageRace message = new MessageRace(RaceCommand.InitSettings, raceNumber, carNames, true);
		outputStream.writeObject(message);
	}
	
	/**
	 * Sends the race controller his chosen cars and their properties.
	 * @throws IOException
	 */
	private void sendCars(MessageRace message) throws IOException {
		String[] carNames = message.getCarNames().clone();
		String[] carMakes = new String[message.getCarNames().length];
		String[] carSizes = new String[message.getCarNames().length];
		String[] carColors = new String[message.getCarNames().length];
		String[] carTypes = new String[message.getCarNames().length];

		for(int i = 0; i < carNames.length; i++) {
			Car car = database.getCarProps(carNames[i]);
			carMakes[i] = car.getMake();
			carSizes[i] = car.getSize();
			carColors[i] = car.getColor();
			carTypes[i] = car.getType();
		}
		MessageRace messageRace = new MessageRace(RaceCommand.CarSettings, message.getRaceNumber(),
				carNames, carMakes, carSizes, carColors, carTypes, true);
		outputStream.writeObject(messageRace);
	}
	
	/**
	 * Inserts new race-car rows to the car race result table when a new race has started.
	 * @param raceNumber the race that has just started.
	 * @param carNames the cars that participate in the race.
	 */
	private void updateDBNewRace(int raceNumber, String[] carNames) {
		for(String carName : carNames)
			database.insertCarRaceResult(raceNumber, carName, 0);
	}
	
	/**
	 * Updates car speeds.
	 * @param message
	 */
	private void setSpeeds(MessageRace message) {
		double[] carSpeeds = message.getCarSpeeds();
		for(int i = 0; i < carSpeeds.length; i++) {
			//cars[i].setSpeed(carSpeeds[i]);
			int j = i;
			Platform.runLater(()-> {
				carLog.printMsg("Race no. " + message.getRaceNumber() + ", Car " + message.getCarNames()[j] + ": speed changed to " + (int)carSpeeds[j] + " at " + dateFormat.format(new Date()));
			});
		}
	}
	
	/**
	 * Handles incoming messages from the race controller.
	 * @param message the race message.
	 */
	private void handleMessage(MessageRace message) throws IOException {
		switch(message.getCommand()) {
		case Connect:
			
			break;
		case Disconnect:
			
			break;
		case Start:
			Platform.runLater(() -> {
				carLog.printMsg("Race no. " + raceNumber + " started at " + dateFormat.format(new Date()) + "!");
			});
			database.updateRaceState(raceNumber, 4);
			mainServer.updateGamblersRaces();
			break;
		case End:
			processRaceResults(message);
			break;
		case ChangeSpeed:
			setSpeeds(message);
			break;
		case InitSettings:
			sendCarNames();
			break;
		case CarSettings:
			updateDBNewRace(message.getRaceNumber(), message.getCarNames());
			mainServer.updateGamblersRaces();
			sendCars(message);
			break;
		default: 
			
			break;
		}
	}
	
	/**
	 * Processes race results when the race has ended.
	 * @param message
	 */
	private void processRaceResults(MessageRace message) {
		for(int i = 0; i < message.getCarNames().length; i++) {
			//	Car speeds are used to store car positions.
			database.updateCarRaceResult(raceNumber, message.getCarNames()[i], (int)message.getCarSpeeds()[i]);
		}
		mainServer.getWaitingRaces().remove(raceNumber);
		Platform.runLater(() -> {
		carLog.printMsg("Race no. " + raceNumber + " ended at " + dateFormat.format(new Date()) + ", Car " + message.getCarNames()[0] + " won!");
		});
		winningCar = message.getCarNames()[0];
		database.updateRaceState(raceNumber, 5);
		mainServer.openNewRace();
		mainServer.startNewRace();
		
		//	TODO: update the main server to calculate revenue for each gambler,
		// 	and update GamblerRaceResult accordingly (also update the winning player's balance).
		try {
			outputStream.writeObject(new MessageRace(RaceCommand.Disconnect, true));
			shutDownHandler();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateBetsAndRevenues() {
		totalBets = database.getRaceTotalBets(raceNumber);
		systemRevenue = (int)(totalBets * 0.05);
		totalBets = (int)(totalBets * 0.95);
		database.setSystemRaceRevenue(raceNumber, systemRevenue);
		HashMap<Integer, Integer> winningBets = database.getWinningBets(raceNumber, winningCar);
	}
	
	private void shutDownHandler() throws IOException {
		raceConnected = false;
		inputStream.close();
		outputStream.close();
		outputStream = null;
		clientSocket.close();
	}
	
	@Override
	public void serverDisconnection() {
		try {
			if (outputStream != null) {
				outputStream.writeObject(new MessageRace(RaceCommand.Disconnect, true));
				shutDownHandler();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Signals the race controller to start the race.
	 */
	@Override
	public void startRace() {
		MessageRace message = new MessageRace(RaceCommand.Start, true);
		try {
			outputStream.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public int getRaceNumber() {
		return raceNumber;
	}

}
