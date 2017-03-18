package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Entities.Car;
import Entities.MessageRace;
import Entities.Race;
import Entities.RaceCommand;
import javafx.application.Platform;

public class HandlerRace implements Runnable, MainServerListener{

	private Socket clientSocket;
	private boolean raceConnected;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private int raceNumber;
	private CarRaceServer mainServer;
	private Database database;
	private CarLog carLog;
	private Car[] cars;
	private Race race;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
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
			e.printStackTrace();
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
			
			break;
		case End:
			
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
	
	@Override
	public void serverDisconnection() {
		// TODO Auto-generated method stub	
	}

}
