package Gambler;

import java.io.ObjectOutputStream;

import Entities.Gambler;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GamblerView {
		
	private Stage mainStage;
	private Scene mainScene;
	private AnchorPane mainPane;
	private double screenWidth, screenHeight;
	private ObjectOutputStream outputStreamToServer;
	
	public GamblerView(ObjectOutputStream outputStreamToServer) {
		this.outputStreamToServer = outputStreamToServer;
	}

	public GamblerLogin getGamblerLoginPanel() {
		return gamblerLoginPanel;
	}

	public void setGamblerLoginPanel(GamblerLogin gamblerLoginPanel) {
		this.gamblerLoginPanel = gamblerLoginPanel;
	}

	public GamblerRegister getGamblerRegistrationPanel() {
		return gamblerRegistrationPanel;
	}

	public void setGamblerRegistrationPanel(GamblerRegister gamblerRegistrationPanel) {
		this.gamblerRegistrationPanel = gamblerRegistrationPanel;
	}

	public GamblerMainWin getGamblerMainPanel() {
		return gamblerMainPanel;
	}

	public void setGamblerMainPanel(GamblerMainWin gamblerMainPanel) {
		this.gamblerMainPanel = gamblerMainPanel;
	}

	private GamblerLogin gamblerLoginPanel;
	private GamblerRegister gamblerRegistrationPanel;
	private GamblerMainWin gamblerMainPanel;
		
	public GamblerView(){
		
		//Screen Size.
		screenWidth = Screen.getPrimary().getVisualBounds().getWidth()*0.35;
		screenHeight = Screen.getPrimary().getVisualBounds().getHeight()*0.5;
		
		//Initialization of main View
		mainStage = new Stage();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane, screenWidth, screenHeight);
		
		//Initialization of panels into 'mainPane'
		gamblerLoginPanel = new GamblerLogin((int)screenWidth, (int)screenHeight);		
		gamblerRegistrationPanel = new GamblerRegister((int)screenWidth, (int)screenHeight);
		gamblerMainPanel = new GamblerMainWin((int)screenWidth, (int)screenHeight);
		
		//Set Click events to panels.
		gamblerLoginPanel.setEventHandler(clickListener);
		gamblerRegistrationPanel.setEventHandler(clickListener);
		gamblerMainPanel.setEventHandler(clickListener);
		
		//Set startUp (Login) panel on mainPane.
		mainPane.getChildren().add(gamblerLoginPanel);
		
		//Set up properties of mainStage
		Image appIcon = new Image(GamblerView.class.getResource("/Server/resources/icon2.png").toExternalForm(),225,225,true,true);
		mainStage.getIcons().add(appIcon);
		mainStage.setTitle("CarRace Gambler");
		mainStage.setAlwaysOnTop(true);
		mainStage.setScene(mainScene);
		mainStage.show();
	}
	
	//Mouse listener, gets events from panels of gambler.
	public final EventHandler<MouseEvent> clickListener = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent e) {

			GamblerButton btnClicked = (GamblerButton)e.getSource();

			switch (btnClicked.getButtonId()) {
			case Login:
				
				//Check gambler out in the dataBase  , get gambler,			
				//if (successful):
				Gambler testGambler = new Gambler(00, "Test", "pass", 1000);
				//
				gamblerMainPanel.setGambler(testGambler);
				//Check gambler out  of in the dataBase/			
				//if (successful):
				mainPane.getChildren().remove(gamblerLoginPanel);
				mainPane.getChildren().add(gamblerMainPanel);
				//else: gamblerLoginPanel.showErrorMessage(String message)
							
				break;

			case Cancel:
				mainPane.getChildren().remove(gamblerRegistrationPanel);
				mainPane.getChildren().add(gamblerLoginPanel);
				break;
				
			case goToRegistration:	
				mainPane.getChildren().remove(gamblerLoginPanel);
				mainPane.getChildren().add(gamblerRegistrationPanel);
				break;
				
			case Exit:
				//LOG OUT Gambler, change status to 'offLine'
				mainPane.getChildren().remove(gamblerMainPanel);
				mainPane.getChildren().add(gamblerLoginPanel);
				
				break;
			default:
				break;
			}
		}
	};
	

}
