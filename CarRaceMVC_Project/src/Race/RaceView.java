package Race;
import Entities.Car;
import Entities.RaceCommand;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class RaceView {
	private Car[] cars;
	private RaceController raceController;
	private BorderPane border_pane;
	private GridPane cars_grid;
	private CarPane[] carPanes = new CarPane[RaceCommand.TotalNumOfCars.ordinal()];

	public RaceView(int raceNumber) {
		border_pane = new BorderPane();
		createCarsGrid();
		border_pane.setCenter(cars_grid);
		Stage stg = new Stage();
		Scene scene = new Scene(border_pane, 750, 500);
		//	Saves the stage object reference at the controller to show error messages.
		createAllTimelines();
		stg.setScene(scene);
		stg.setTitle("CarRaceView" + raceNumber);
		stg.setAlwaysOnTop(true);
		stg.show();
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) { // TODO																								// stub
				setCarPanesMaxWidth(newValue.doubleValue());
			}
		});
	}

	/**
	 * Sets the car properties in every car pane.
	 * @param cars the array of cars in this race.
	 */
	public void setCarsProps(Car[] cars) {
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
			CarPane carPane = new CarPane();
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
}
