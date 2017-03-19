package Race;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import Entities.*;

public class RaceController implements Runnable {

	private final int speedRangeMin = 10, speedRangeMax = 15, timeBetweenSpeedChange = 30000;
	private Socket clientSocket;
	private ObjectOutputStream outputStreamToServer;
	private ObjectInputStream inputStreamFromServer;		
	private Random random;
	private boolean connectedToServer = false;
	private RaceView raceView;
	private String[] carNames;
	private int raceNumber;
	private Timer speedChangeTimer, raceTimer;

	@Override
	public void run() {
		try {
			clientSocket = new Socket("localhost", 8888);
			outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
			connectedToServer = true;
			raceView = new RaceView(this);
			initReceiverFromServer();
			requestCarNames();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	private void requestCarNames() throws IOException {
		MessageRace message = new MessageRace(RaceCommand.InitSettings, true);
		outputStreamToServer.writeObject(message);
	}
	
	/**
	 * Selects cars for this race.
	 */
	private void selectCars(MessageRace message) throws IOException {
		random = new Random(message.getRaceNumber());
		ArrayList<Integer> chosenCarsNumbers = new ArrayList<>();
		raceNumber = message.getRaceNumber();
		raceView.setRaceNumber(raceNumber);
		carNames = new String[RaceCommand.TotalNumOfCars.ordinal()];
		for(int i = 0; i < carNames.length; i++) {
			int j = random.nextInt(message.getCarNames().length);
			if (!chosenCarsNumbers.contains(j)) {
				chosenCarsNumbers.add(j);
				carNames[i] = new String(message.getCarNames()[j]);
			}
			else 
				i--;
		}
		message.setCommand(RaceCommand.CarSettings);
		message.setCarNames(carNames);
		outputStreamToServer.writeObject(message);
	}

	/**
	 * Initialises the cars at the view after receiving their properties from the server (handler).
	 * @param message the race message.
	 */
	private void initCars(MessageRace message) {
		Car[] cars = new Car[RaceCommand.TotalNumOfCars.ordinal()];
		for(int i = 0; i < cars.length; i++) {
			cars[i] = new Car(message.getCarNames()[i], message.getCarMakes()[i], 
					message.getCarSizes()[i], message.getCarColors()[i], message.getCarTypes()[i]);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		raceView.setCarsProps(cars);
	}
	
	/**
	 * Updates the model (race handler) with the new speed rates.
	 * @param carSpeeds the new speed rates.
	 */
	private void sendSpeedChangesToModel(double[] carSpeeds) {
		MessageRace message = new MessageRace(RaceCommand.ChangeSpeed, raceNumber, carNames, carSpeeds, true);
		try {
			outputStreamToServer.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initReceiverFromServer() {

		new Thread(() -> {
			try {
				while (connectedToServer) {
					MessageRace message = (MessageRace) inputStreamFromServer.readObject();
					processMessage(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
			finally {
				try {
					clientSocket.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void processMessage(MessageRace message) throws IOException {
		switch(message.getCommand()){
			case Connect:

				break;
			case Disconnect:
				shutdownClient();
				break;
			case Start:
				startRace();
				try {
					outputStreamToServer.writeObject(new MessageRace(RaceCommand.Start, true));
				} catch (IOException e) {
					e.printStackTrace();
				}
				raceTimer = new Timer();
				raceTimer.schedule(new RaceEndTask(), 60*1000 + 2);
				speedChangeTimer = new Timer();
				speedChangeTimer.schedule(new SpeedChangeTask(), 1, timeBetweenSpeedChange);
				break;
			case End:
				
				break;
			case InitSettings:
				selectCars(message);
				break;
			case CarSettings:
				initCars(message);
				break;
			default:
				
				break;
 		}
	}
	
	/**
	 * Starts the race.
	 */
	private void startRace() {
		raceView.createAllTimelines();
	}
	
	/**
	 * Sends race results back to the race model (handler).
	 * @param results the race results.
	 */
	public void sendResults(HashMap<String, Double> results) {
		String[] cars = new String[RaceCommand.TotalNumOfCars.ordinal()];
		double[] positions = new double[RaceCommand.TotalNumOfCars.ordinal()];
		int i = 0;
		Stream<Map.Entry<String, Double>> sorted =
			    results.entrySet().stream()
			       .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		Iterator<Map.Entry<String, Double>> iterator = sorted.iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, Double> entry = iterator.next();
		    cars[i] = entry.getKey();
		    positions[i] = i + 1;
		    i++;
		}
		MessageRace message = new MessageRace(RaceCommand.End, raceNumber, cars, positions, true);
		try {
			outputStreamToServer.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes all connections and attempts to shut down the race controller upon receiving a Disconnect message from the server (handler).
	 * @throws IOException
	 */
	private void shutdownClient() throws IOException {
		raceView.closeView();
		connectedToServer = false;
		outputStreamToServer.close();
		inputStreamFromServer.close();
		clientSocket.close();
	}
	
	/**
	 * The timer task that is responsible of changing the speeds of all cars in a running race,
	 * every 30 seconds.
	 *
	 */
	class SpeedChangeTask extends TimerTask {
		@Override
		public void run() {
			double[] newCarSpeeds = new double[raceView.getCars().length];
			for(int i = 0; i < raceView.getCars().length; i++) {
				double newSpeed = speedRangeMin + (speedRangeMax - speedRangeMin) * random.nextDouble();
				raceView.getCars()[i].setSpeed(newSpeed);
				newCarSpeeds[i] = newSpeed;
			}
			sendSpeedChangesToModel(newCarSpeeds);
		}
	}
	
	/**
	 * The timer task that stops the race (after the song time + 2 seconds have passed).
	 *
	 */
	class RaceEndTask extends TimerTask {
		@Override
		public void run() {
			speedChangeTimer.cancel();
			raceTimer.cancel();
			raceView.stopRace();
		}
	}
}