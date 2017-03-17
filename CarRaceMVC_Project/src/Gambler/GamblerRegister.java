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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GamblerRegister extends StackPane implements IGamblerPanelMessage{
	
	
	private Label messageLbl;
	private GamblerTextField txtName,txtPassword,txtConfPassword;
	private final int fieldTypeText=0, fieldTypePassword=1;
	private GamblerButton registerBtn, CancelBtn;
	
	public GamblerRegister(int panelWidth,int panelHeight) {
		
		setPrefWidth(panelWidth);
		setPrefHeight(panelHeight);
		
		setStyle( "-fx-background-image: url(/Gambler/resources/gamblerBackground1.jpg);"
	            + "-fx-background-size: cover;" );

		
		VBox mainVbox = new VBox();
		mainVbox.setAlignment(Pos.TOP_CENTER);
		mainVbox.setPadding(new Insets(panelHeight*0.1, 0, 0, 0));
		mainVbox.setMaxWidth(panelWidth*0.7);
		mainVbox.setSpacing(panelHeight*0.01);
		
		messageLbl = new Label();
		messageLbl.setFont(new Font("Serif", 24));
		messageLbl.setStyle("-fx-font-weight: bold;");
		
		VBox gridsContainer = new VBox();
		gridsContainer.setBackground(new Background(new BackgroundFill(Color.web("#ffffff70"), CornerRadii.EMPTY, Insets.EMPTY)));
		
		txtName = new GamblerTextField("Name : ",fieldTypeText);
		txtPassword = new GamblerTextField("Password : ",fieldTypePassword);
		txtConfPassword = new GamblerTextField("Confirm Password : ",fieldTypePassword);
		
		gridsContainer.getChildren().addAll(txtName, txtPassword, txtConfPassword);	
		gridsContainer.setSpacing(2);
		gridsContainer.setPadding(new Insets(5,5,5,5));
		
		registerBtn = new GamblerButton(ButtonId.Registration,"register.png", panelWidth*0.5, panelHeight*0.35);
		CancelBtn = new GamblerButton(ButtonId.Cancel,"cancel.png", panelWidth*0.3, panelHeight*0.25);
		
		HBox buttonsPanel = new HBox();
		buttonsPanel.setSpacing(3);
		buttonsPanel.setAlignment(Pos.TOP_CENTER);
		buttonsPanel.getChildren().addAll(CancelBtn, registerBtn);
		
		mainVbox.getChildren().addAll(messageLbl, gridsContainer, buttonsPanel);

		StackPane.setAlignment(mainVbox, Pos.CENTER);
		getChildren().add(mainVbox);
	}
	
	public void setEventHandler(EventHandler<MouseEvent> mouseEventHandler){
		registerBtn.setOnMousePressed(mouseEventHandler);
		CancelBtn.setOnMousePressed(mouseEventHandler);
	}

	public String getName(){
		return((TextField)txtName.getTextControl()).getText();
	}
	
	public String getPassword(){
		return((PasswordField)txtPassword.getTextControl()).getText();
	}
	
	public String getConfPassword() {
		return((PasswordField)txtConfPassword.getTextControl()).getText();
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
		((TextField)txtName.getTextControl()).clear();
		((PasswordField)txtPassword.getTextControl()).clear();
		((PasswordField)txtConfPassword.getTextControl()).clear();	
	}
}
