package Gambler;

import Entities.Gambler;
import Gambler.GamblerButton.ButtonId;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GamblerMainWin extends StackPane implements IGamblerPanelMessage{
	
	private Image backgroundImage;
	private GamblerComboBox raceCombo, carCombo;
	private GamblerButton betBtn, exitBtn;
	private GamblerTextField betTxt;
	private Label messageLbl, gamblerInfoLbl;
	private Gambler gambler = new Gambler(0, "empty", "Empty");
	
	public GamblerMainWin(int panelWidth,int panelHeight) {
		
		setStyle( "-fx-background-image: url(/Gambler/resources/gamblerBackground1.jpg);"
	            + "-fx-background-size: cover;" );
		//backgroundImage = new Image(GamblerLogin.class.getResource("/Gambler/resources/gamblerBackground1.jpg").toExternalForm()); 
		betBtn = new GamblerButton(ButtonId.Bet,"bet.png", 180, 170, panelWidth*0.40, panelHeight*0.15);				
		exitBtn = new GamblerButton(ButtonId.Exit,"exit.png", 190, 150, panelWidth*0.25, panelHeight*0.15);				
		
		raceCombo = new GamblerComboBox("Race: ");
		carCombo = new GamblerComboBox("Car: ");
		betTxt = new GamblerTextField("Your Bet: ", 0);
		messageLbl = new Label();
		
		gamblerInfoLbl = new Label();
		gamblerInfoLbl.setFont(new Font("Serif", 20));
		gamblerInfoLbl.setTextFill(Color.DEEPSKYBLUE);
		gamblerInfoLbl.setStyle("-fx-font-weight: bold;");

		setPrefWidth(panelWidth);
		setPrefHeight(panelHeight);
		//setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

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

	public void setGambler(Gambler gambler) {
		this.gambler = gambler;
		gamblerInfoLbl.setText("Welcome " + gambler.getName() + ". Your balance: " + gambler.getBalance());

	}

	public void setEventHandler(EventHandler<MouseEvent> mouseEventHandler){
		exitBtn.setOnMousePressed(mouseEventHandler);
		betBtn.setOnMousePressed(mouseEventHandler);
	}

	public String getBet(){
		return "Bet";
	}
	
	
	public void showMessage(String message, MessageColor color){
		if(color==MessageColor.Red)
			messageLbl.setTextFill(Color.RED);
		else
			messageLbl.setTextFill(Color.GREEN);
		messageLbl.setText(message);
	}
}
