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

public class GamblerLogin extends StackPane{
	
	private final int fieldTypeText=0, fieldTypePassword=1;
	private GamblerTextField txtName,txtPassword;
	private Label messageLbl;
	private Image backgroundImage;
	GamblerButton regiserBtn,loginBtn;
	
	public GamblerLogin(int panelWidth,int panelHeight) {
	
		txtName = new GamblerTextField("Name : ",fieldTypeText);
		txtPassword = new GamblerTextField("Password : ",fieldTypePassword);
		messageLbl = new Label();
		backgroundImage = new Image(GamblerLogin.class.getResource("/Gambler/resources/gamblerBackground1.jpg").toExternalForm()); 
		loginBtn = new GamblerButton(ButtonId.Login,"loginNew.png", 180, 170, panelWidth*0.6, panelHeight*0.12);				
		regiserBtn = new GamblerButton(ButtonId.goToRegistration,"register.png", 190, 150, panelWidth*0.5, panelHeight*0.3);
		
		setPrefWidth(panelWidth);
		setPrefHeight(panelHeight);
		setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

		
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
	
		loginBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {

			}
		});
		
		regiserBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {

			}
		});

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
	
	public void showErrorMessage(String message){
		messageLbl.setTextFill(Color.RED);
		messageLbl.setText(message);
	}
	
}
