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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GamblerRegister extends StackPane{
	
	
	private Label messageLbl;
	private GamblerTextField txtName,txtPassword,txtConfPassword;
	private final int fieldTypeText=0, fieldTypePassword=1;
	private GamblerButton registerBtn, CancelBtn;
	private Image backgroundImage;
	
	public GamblerRegister(int panelWidth,int panelHeight) {
		
		setPrefWidth(panelWidth);
		setPrefHeight(panelHeight);
		
		backgroundImage = new Image(GamblerLogin.class.getResource("/Gambler/resources/registerBackground1.jpg").toExternalForm()); 		
		setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, 
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

		
		VBox mainVbox = new VBox();
		mainVbox.setAlignment(Pos.TOP_CENTER);
		mainVbox.setPadding(new Insets(panelHeight*0.1, 0, 0, 0));
		mainVbox.setMaxWidth(panelWidth*0.7);
		mainVbox.setSpacing(panelHeight*0.01);
		
		messageLbl = new Label();
		
		VBox gridsContainer = new VBox();
		gridsContainer.setBackground(new Background(new BackgroundFill(Color.web("#ffffff70"), CornerRadii.EMPTY, Insets.EMPTY)));
		
		txtName = new GamblerTextField("Name : ",fieldTypeText);
		txtPassword = new GamblerTextField("Password : ",fieldTypeText);
		txtConfPassword = new GamblerTextField("Confirm Password : ",fieldTypePassword);
		
		gridsContainer.getChildren().addAll(txtName, txtPassword, txtConfPassword);	
		gridsContainer.setSpacing(2);
		gridsContainer.setPadding(new Insets(5,5,5,5));
		
		registerBtn = new GamblerButton(ButtonId.Registration,"register.png", 278, 80, panelWidth*0.5, panelHeight*0.4);
		CancelBtn = new GamblerButton(ButtonId.Cancel,"cancel.png", 278, 80, panelWidth*0.3, panelHeight*0.2);
		
		HBox buttonsPanel = new HBox();
		buttonsPanel.setSpacing(2);
		buttonsPanel.getChildren().addAll(CancelBtn, registerBtn);
		buttonsPanel.setAlignment(Pos.CENTER);
		
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
	
	public void showErrorMessage(String message){
		messageLbl.setTextFill(Color.RED);
		messageLbl.setText(message);
	}
}
