package Gambler;

import Gambler.GamblerButton.ButtonId;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GamblerLogin extends StackPane implements IGamblerPanelMessage{
	
	private final int fieldTypeText=0, fieldTypePassword=1;
	private GamblerTextField txtName,txtPassword;
	private Label messageLbl;
	private GamblerButton regiserBtn,loginBtn;
	
	public GamblerLogin(int panelWidth,int panelHeight) {
	
		txtName = new GamblerTextField("Name : ",fieldTypeText);
		txtPassword = new GamblerTextField("Password : ",fieldTypePassword);
		
		messageLbl = new Label();
		messageLbl.setFont(new Font("Serif", 24));
		messageLbl.setStyle("-fx-font-weight: bold;");
		
		setStyle(
	            "-fx-background-image: url(/Gambler/resources/gamblerBackground1.jpg);"
	            + "-fx-background-size: cover;" );
	
		loginBtn = new GamblerButton(ButtonId.Login,"loginNew.png", panelWidth*0.6, panelHeight*0.12);				
		regiserBtn = new GamblerButton(ButtonId.goToRegistration,"register.png", panelWidth*0.5, panelHeight*0.3);
		
		setPrefWidth(panelWidth);
		setPrefHeight(panelHeight);
		
		VBox mainVbox = new VBox();
		mainVbox.setAlignment(Pos.TOP_CENTER);
		mainVbox.setPadding(new Insets(panelHeight*0.2, 0, 0, 0));
		mainVbox.setMaxWidth(panelWidth*0.65);
		mainVbox.setSpacing(panelHeight*0.02);
						
		VBox gridsContainer = new VBox();
		gridsContainer.setBackground(new Background(new BackgroundFill(Color.web("#ffffff70"), CornerRadii.EMPTY, Insets.EMPTY)));
		gridsContainer.setSpacing(panelHeight*0.02);
		gridsContainer.setPadding(new Insets(5,5,5,5));
		gridsContainer.getChildren().addAll(txtName, txtPassword);

		VBox buttonsPanel = new VBox();
		buttonsPanel.getChildren().addAll(loginBtn,regiserBtn);
		buttonsPanel.setAlignment(Pos.CENTER);
		
		mainVbox.getChildren().addAll(messageLbl,gridsContainer, buttonsPanel);
				
	    StackPane.setAlignment(mainVbox, Pos.CENTER);
		getChildren().add(mainVbox);
	}
	
	public void setEventHandler(EventHandler<MouseEvent> mouseEventHandler){
		regiserBtn.setOnMousePressed(mouseEventHandler);
		loginBtn.setOnMousePressed(mouseEventHandler);
	}

	public String getName(){
		return((TextField)txtName.getTextControl()).getText();
	}
	
	public String getPassword(){
		return((PasswordField)txtPassword.getTextControl()).getText();
	}
	
	public void showMessage(String message, MessageColor color){
		if(color==MessageColor.Red)
			messageLbl.setTextFill(Color.RED);
		else
			messageLbl.setTextFill(Color.LAWNGREEN);
		messageLbl.setText(message);
	}

	@Override
	public void clearPanel() {
		messageLbl.setText("");		
		((TextField)txtName.getTextControl()).clear();;
		((PasswordField)txtPassword.getTextControl()).clear();
	}
	
}
