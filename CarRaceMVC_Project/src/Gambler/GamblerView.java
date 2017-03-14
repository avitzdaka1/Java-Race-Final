package Gambler;

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
	
	private GamblerLogin gamblerLoginPanel;
	private GamblerRegister gamblerRegistrationPanel;
		
	public GamblerView(){
		
		screenWidth = Screen.getPrimary().getVisualBounds().getWidth()*0.35;
		screenHeight = Screen.getPrimary().getVisualBounds().getHeight()*0.5;
		
		mainStage = new Stage();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane, screenWidth, screenHeight);
		
		gamblerLoginPanel = new GamblerLogin((int)screenWidth, (int)screenHeight);		
		gamblerRegistrationPanel = new GamblerRegister((int)screenWidth, (int)screenHeight);
		
		gamblerLoginPanel.setEventHandler(clickListener);
		gamblerRegistrationPanel.setEventHandler(clickListener);
		
		mainPane.getChildren().add(gamblerLoginPanel);
		
		Image appIcon = new Image(GamblerView.class.getResource("/Server/resources/icon2.png").toExternalForm(),225,225,true,true);
		mainStage.getIcons().add(appIcon);
		mainStage.setTitle("CarRace Gambler");
		mainStage.setAlwaysOnTop(true);
		mainStage.setScene(mainScene);
		mainStage.show();
	}
	
	//Muse listener
	public final EventHandler<MouseEvent> clickListener = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent e) {

			GamblerButton btnClicked = (GamblerButton)e.getSource();

			switch (btnClicked.getButtonId()) {
			case Login:
				
				break;

			case Cancel:
				mainPane.getChildren().remove(gamblerRegistrationPanel);
				mainPane.getChildren().add(gamblerLoginPanel);
				break;
				
			case Registration:	
				mainPane.getChildren().remove(gamblerLoginPanel);
				mainPane.getChildren().add(gamblerRegistrationPanel);
				break;
				
			case Exit:
				
				break;
			default:
				break;
			}
		}
	};
	

}
