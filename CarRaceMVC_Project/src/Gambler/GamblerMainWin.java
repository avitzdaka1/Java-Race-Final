package Gambler;

import java.util.ArrayList;

import Entities.Gambler;
import Gambler.GamblerButton.ButtonId;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GamblerMainWin extends StackPane implements IGamblerPanelMessage{
	
	private GamblerComboBox raceCombo, carCombo;
	private GamblerButton betBtn, exitBtn;
	private GamblerTextField betTxt;
	private Label messageLbl, gamblerInfoLbl;
	private Gambler gambler = new Gambler(0, "empty", "Empty");
	private ObservableList<String> races = FXCollections.observableArrayList();
	private ObservableList<String> cars = FXCollections.observableArrayList();
	
	@SuppressWarnings("unchecked")
	public GamblerMainWin(int panelWidth,int panelHeight) {
		
		setStyle( "-fx-background-image: url(/Gambler/resources/gamblerBackground1.jpg);"
	            + "-fx-background-size: cover;" );
		betBtn = new GamblerButton(ButtonId.Bet,"bet.png", panelWidth*0.40, panelHeight*0.15);				
		exitBtn = new GamblerButton(ButtonId.Exit,"exit.png", panelWidth*0.25, panelHeight*0.15);				
		
		raceCombo = new GamblerComboBox("Race: ");		
		raceCombo.setOptionsList(races);
        
		((ComboBox<String>) raceCombo.getComboControl()).getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Object>() {
					@Override
					public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
						carCombo.getOptionsList().clear();
						int race_index = raceCombo.getOptionsList().indexOf(newValue);
						for (int i = race_index*5; i < race_index*5 + 5; i++)
							carCombo.getOptionsList().add(cars.get(i));						
					}
				});
		
		carCombo = new GamblerComboBox("Car: ");
		betTxt = new GamblerTextField("Your Bet: ", 0);
		
		messageLbl = new Label();
		messageLbl.setFont(new Font("Serif", 24));
		messageLbl.setStyle("-fx-font-weight: bold;");
		
		gamblerInfoLbl = new Label();
		gamblerInfoLbl.setFont(new Font("Serif", 20));
		gamblerInfoLbl.setTextFill(Color.DEEPSKYBLUE);
		gamblerInfoLbl.setStyle("-fx-font-weight: bold;");

		setPrefWidth(panelWidth);
		setPrefHeight(panelHeight);

		VBox mainVbox = new VBox();
		mainVbox.setAlignment(Pos.TOP_CENTER);
		mainVbox.setPadding(new Insets(panelHeight*0.2, 0, 0, 0));
		mainVbox.setMaxWidth(panelWidth*0.65);
		mainVbox.setSpacing(panelHeight*0.02);		
		
		VBox gridsContainer = new VBox();
		gridsContainer.setBackground(new Background(new BackgroundFill(Color.web("#ffffff70"), CornerRadii.EMPTY, Insets.EMPTY)));
		gridsContainer.setSpacing(panelHeight*0.05);
		gridsContainer.setPadding(new Insets(10,10,10,10));
		gridsContainer.getChildren().addAll(raceCombo, carCombo, betTxt);
		
		HBox buttonsPanel = new HBox();
		buttonsPanel.getChildren().addAll(exitBtn, betBtn);
		buttonsPanel.setAlignment(Pos.CENTER);
		
		mainVbox.getChildren().addAll(gamblerInfoLbl, messageLbl,gridsContainer, buttonsPanel);
						
	    StackPane.setAlignment(buttonsPanel, Pos.CENTER);
		getChildren().add(mainVbox);
	}
	
	public Gambler getGambler() {
		return gambler;
	}
	
	public int getGamblerId() {
		return gambler.getId();
	}

	public void setGambler(Gambler gambler) {
		this.gambler = gambler;
		showGamblerInfo(gambler.getName(),gambler.getBalance());
	}
	
	public void showGamblerInfo(String name, int balance){
		gamblerInfoLbl.setText("Welcome " + name + ". Your balance: " + balance);
	}

	public void setEventHandler(EventHandler<MouseEvent> mouseEventHandler){
		exitBtn.setOnMousePressed(mouseEventHandler);
		betBtn.setOnMousePressed(mouseEventHandler);
	}

	public void updateRacesAndCars(int[] races, String[] cars){
		for(int race:races)
			this.races.add(String.valueOf(race));
		for(String car:cars)
			this.cars.add(car);		
		raceCombo.setOptionsList(this.races);
	}
	
/*	public void requestCarsOfRace(int place) {
		for (int i = place; i < place + 5; i++)
			carCombo.getOptionsList().add(this.cars.get(i));
	}*/
	
	public int getRaceNumber(){
		if(!raceCombo.getSelectedOption().isEmpty())
			return Integer.parseInt(raceCombo.getSelectedOption());
		else 
			return -1;
	}
	
	public String getCarName(){
		if(!carCombo.getSelectedOption().isEmpty())
			return carCombo.getSelectedOption();
		else 
			return null;
	}
	
	public int getBet(){
		String bet = ((TextField)betTxt.getTextControl()).getText();
		if(!bet.isEmpty() && bet.matches("^[1-9]\\d*$"))
			return Integer.parseInt(bet);
		else
			return -1;
	}
		
	public void showMessage(String message, MessageColor color){
		if(color==MessageColor.Red)
			messageLbl.setTextFill(Color.RED);
		else
			messageLbl.setTextFill(Color.GREEN);
		messageLbl.setText(message);
	}
	
	@Override
	public void clearPanel() {
		messageLbl.setText("");	
		((TextField)betTxt.getTextControl()).clear();
		cars.clear();
	//	races.clear();
	//	carCombo.getOptionsList().clear();		
	//	raceCombo.getOptionsList().clear();
		
	}
}
