package Gambler;


import Entities.Gambler;
import Entities.GamblerCommand;
import Entities.MessageGambler;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GamblerView implements GamblerListener{
		
	private Scene mainScene;
	private Stage mainStage;
	private AnchorPane mainPane;
	private double screenWidth, screenHeight;

	private GamblerLogin gamblerLoginPanel;
	private GamblerRegister gamblerRegistrationPanel;
	private GamblerMainWin gamblerMainPanel;
	
	private GamblerClient client;
	
	public GamblerView(GamblerClient client) {
		this.client = client;
		// Screen Size.
		screenWidth = Screen.getPrimary().getVisualBounds().getWidth() * 0.45;
		screenHeight = Screen.getPrimary().getVisualBounds().getHeight() * 0.6;

		// Initialization of main View
		Platform.runLater(() -> {
			mainStage = new Stage();
			mainPane = new AnchorPane();
			mainScene = new Scene(mainPane, screenWidth, screenHeight);

			// Initialization of panels into 'mainPane'
			gamblerLoginPanel = new GamblerLogin((int) screenWidth, (int) screenHeight);
			gamblerRegistrationPanel = new GamblerRegister((int) screenWidth, (int) screenHeight);
			gamblerMainPanel = new GamblerMainWin((int) screenWidth, (int) screenHeight);

			// Set Click events to panels.
			gamblerLoginPanel.setEventHandler(clickListener);
			gamblerRegistrationPanel.setEventHandler(clickListener);
			gamblerMainPanel.setEventHandler(clickListener);

			// Set startUp (Login) panel on mainPane.
			mainPane.getChildren().add(gamblerLoginPanel);

			// Set up properties of mainStage
			Image appIcon = new Image(GamblerView.class.getResource("/Server/resources/icon2.png").toExternalForm(), 225,
					225, true, true);
			mainStage.getIcons().add(appIcon);
			mainStage.setTitle("CarRace Gambler");
			mainStage.setAlwaysOnTop(true);
			mainStage.setScene(mainScene);
			mainStage.setMinWidth(screenWidth * 0.9);
			mainStage.setMinHeight(screenHeight * 0.9);
			mainStage.setMaxWidth(screenWidth);
			mainStage.setMaxHeight(screenHeight);
			mainStage.show();
		});
		
	}
	
	//Mouse listener, gets events from panels of gambler.
	public final EventHandler<MouseEvent> clickListener = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent e) {

			GamblerButton btnClicked = (GamblerButton)e.getSource();

			switch (btnClicked.getButtonId()) {
			
			case Registration:
					String regUsername = gamblerRegistrationPanel.getName();
					String regPassword = gamblerRegistrationPanel.getPassword();
					MessageGambler messageReg = new MessageGambler(GamblerCommand.Register, regUsername, regPassword);
					client.SendGamblerMessage(messageReg);
				break;
				
			case Login:			
				String username = gamblerLoginPanel.getName();
				String password = gamblerLoginPanel.getPassword();				
				MessageGambler message = new MessageGambler(GamblerCommand.Login, username, password);
				client.SendGamblerMessage(message);
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

	public GamblerLogin getGamblerLoginPanel() {
		return gamblerLoginPanel;
	}

	public void setGamblerLoginPanel(GamblerLogin gamblerLoginPanel) {
		this.gamblerLoginPanel = gamblerLoginPanel;
	}

	public GamblerRegister getGamblerRegistrationPanel() {
		return gamblerRegistrationPanel;
	}

	public AnchorPane getMainPane() {
		return mainPane;
	}

	public void setMainPane(AnchorPane mainPane) {
		this.mainPane = mainPane;
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


	@Override
	public void registerSuccess(boolean success) {
		if(success) {
			Platform.runLater(() -> {		
				mainPane.getChildren().remove(gamblerRegistrationPanel);
				mainPane.getChildren().add(gamblerLoginPanel);
			});
		}
		else{
			gamblerLoginPanel.showMessage("Error! Login unsuccessful!");
		}
		
	}
	
	@Override
	public void loginSuccess(Gambler gambler) {
		if(gambler != null) {
			Platform.runLater(() -> {		
				gamblerMainPanel.setGambler(gambler);
				mainPane.getChildren().remove(gamblerLoginPanel);
				mainPane.getChildren().add(gamblerMainPanel);
			});
		}
		else{
			gamblerLoginPanel.showMessage("Error! Login unsuccessful!");
		}
		

	}

	@Override
	public void betPlaceSuccess(int newBalance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void betPlaceFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newRaceStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void raceEnded() {
		// TODO Auto-generated method stub
		
	}

}
