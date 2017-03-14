package Gambler;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GamblerMainWin extends StackPane {
	
	private Image backgroundImage;
	GamblerButton betBtn, cancelBtn;
	private Label messageLbl;
	
	public GamblerMainWin(int panelWidth,int panelHeight) {
		
		backgroundImage = new Image(GamblerLogin.class.getResource("/Gambler/resources/gamblerBackground2.jpg").toExternalForm()); 
		betBtn = new GamblerButton(ButtonId.Bet,"bet.png", 180, 170, panelWidth*0.6, panelHeight*0.12);				
		
		setPrefWidth(panelWidth);
		setPrefHeight(panelHeight);
		setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

		VBox buttonsPanel = new VBox();
		buttonsPanel.getChildren().addAll(cancelBtn,betBtn);
		buttonsPanel.setAlignment(Pos.CENTER);
						
	    StackPane.setAlignment(buttonsPanel, Pos.CENTER);
		getChildren().add(buttonsPanel);
	}
	
	public void setEventHandler(EventHandler<MouseEvent> mouseEventHandler){
		cancelBtn.setOnMousePressed(mouseEventHandler);
		betBtn.setOnMousePressed(mouseEventHandler);
	}

	public String getBet(){
		return "Bet";
	}
	
	
	public void showErrorMessage(String message){
		messageLbl.setTextFill(Color.RED);
		messageLbl.setText(message);
	}

}
