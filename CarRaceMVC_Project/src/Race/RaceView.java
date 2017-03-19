package Race;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import Entities.Car;
import Entities.RaceCommand;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class RaceView {
	
	private Stage stage;
	private Car[] cars;
	private RaceController raceController;
	private BorderPane border_pane;
	private GridPane cars_grid;
	private CarPane[] carPanes = new CarPane[RaceCommand.TotalNumOfCars.ordinal()];
	private HashMap<String, Double> raceResults = new HashMap<>();
	private int raceNumber;



	public RaceView(RaceController raceController) {
		this.raceController = raceController;
		Platform.runLater(() -> {
			border_pane = new BorderPane();
			createCarsGrid();
			border_pane.setCenter(cars_grid);
			stage = new Stage();
			Scene scene = new Scene(border_pane, 750, 500);
			// Saves the stage object reference at the controller to show error
			// messages.
			stage.setScene(scene);
			stage.setTitle("CarRaceView " + raceNumber);
			stage.setAlwaysOnTop(true);
			stage.show();
			scene.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) { // TODO
																														// //
																														// stub
					setCarPanesMaxWidth(newValue.doubleValue());
				}
			});
		});
	}

	/**
	 * Sets the car properties in every car pane.
	 * @param cars the array of cars in this race.
	 */
	public void setCarsProps(Car[] cars) {
		Platform.runLater(() -> {
			stage.setTitle("CarRaceView " + raceNumber);
		});
		this.cars = cars;
		if (cars != null) {
			for(int i = 0; i < cars.length; i++) {
				carPanes[i].setCarModel(cars[i]);
			}
		}
	}

	public Car[] getCars() {
		return cars;
	}

	/**
	 * Creates the cars grid ("the actual race").
	 */
	public void createCarsGrid() {
		cars_grid = new GridPane();
		for (int i = 0; i < carPanes.length; i++) {
			CarPane carPane = new CarPane(this);
			carPanes[i] = carPane;
			cars_grid.add(carPane, 0, i);
		}
		cars_grid.setStyle("-fx-background-color: beige");
		cars_grid.setGridLinesVisible(true);
		ColumnConstraints column = new ColumnConstraints();
		column.setPercentWidth(100);
		cars_grid.getColumnConstraints().add(column);
		RowConstraints row = new RowConstraints();
		row.setPercentHeight(33);
		for (int i = 0; i < carPanes.length; i++) {
			cars_grid.getRowConstraints().add(row);
		}
	}

	public void createAllTimelines() {
		for(int i = 0; i < carPanes.length; i++) {
			carPanes[i].createTimeline();
		}
	}

	public void stopRace() {
		for(CarPane pane : carPanes) {
			pane.stopTimeline();
		}
	}
	
	public BorderPane getBorderPane() {
		return border_pane;
	}

	public GridPane getCarsGrid() {
		return cars_grid;
	}

	public void setCarPanesMaxWidth(double newWidth) {
		for(int i = 0; i < carPanes.length; i++) {
			carPanes[i].setMaxWidth(newWidth);
		}
	}
	
	public CarPane[] getCarPanes() {
		return carPanes;
	}
	
	/**
	 * Every time a car gets to the finish line it inserts itself to the race results HashMap.
	 * @param carName the car that finished race.
	 * @param distance the distance the car has passed (before the race ended).
	 */
	public void raceEnd(String carName, double distance) {
		raceResults.put(carName, distance);
		if (raceResults.size() == RaceCommand.TotalNumOfCars.ordinal()) {
			//	TODO: Display results on screen for 1 minute.
			raceController.sendResults(raceResults);
		}
	}
	
	public void closeView() {
		Platform.runLater(() -> {
			stage.close();
		});
	}
	
	public int getRaceNumber() {
		return raceNumber;
	}

	public void setRaceNumber(int raceNumber) {
		this.raceNumber = raceNumber;
	}
}
