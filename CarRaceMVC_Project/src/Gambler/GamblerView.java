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
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Gambler view class, in charge of the betting interface the gambler sees when he plays.
 * @author Vitaly Ossipenkov
 * @author Omer Yaari
 *
 */
public class GamblerView implements GamblerListener{
		
	private Scene mainScene;
	private Stage mainStage;
	private AnchorPane mainPane;
	private double screenWidth, screenHeight;

	private GamblerLogin gamblerLoginPanel;
	private GamblerRegister gamblerRegistrationPanel;
	private GamblerMainWin gamblerMainPanel;
	
	private GamblerClient client;
	
	/**
	 * Builds the GamblerView JavaFX application.
	 * @param client the GambleClient that communicates with the handler at the server.
	 */
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
			mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {			
				@Override
				public void handle(WindowEvent event) {
					if(mainPane.getChildren().get(0)==gamblerMainPanel){
						MessageGambler messageLogout = new MessageGambler(GamblerCommand.Logout, 
								client.getCurrentGambler().getName(), client.getCurrentGambler().getPassword());
						client.SendGamblerMessage(messageLogout);
					}				
				}
			});
			
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
	
	/**
	 * Mouse click listener, gets events from panels of gambler.
	 */
	public final EventHandler<MouseEvent> clickListener = new EventHandler<MouseEvent> () {
		public void handle(MouseEvent e) {

			GamblerButton btnClicked = (GamblerButton)e.getSource();

			switch (btnClicked.getButtonId()) {
			
			case Registration:
					String regUsername = gamblerRegistrationPanel.getName();
					String regPassword = gamblerRegistrationPanel.getPassword();
					String regPasswordConfirm = gamblerRegistrationPanel.getConfPassword();
					
					if(regUsername.isEmpty() || regPassword.isEmpty())
						gamblerRegistrationPanel.showMessage("Registration error,\nusername or password is empty!!", MessageColor.Red);
					else if(regUsername.length()>10 || regPassword.length()>10)
						gamblerRegistrationPanel.showMessage("Registration error, username and password lengths\nmust be less then 10 characters!", MessageColor.Red);
					else if (!regPassword.equals(regPasswordConfirm))
						gamblerRegistrationPanel.showMessage("Registration error, passwords don't match!", MessageColor.Red);
					else{
						MessageGambler messageReg = new MessageGambler(GamblerCommand.Register, regUsername, regPassword);
						client.SendGamblerMessage(messageReg);
						// Clear the message after registration
						gamblerRegistrationPanel.clearPanel();
					}
				break;
				
			case Login:			
				String usernameLogin = gamblerLoginPanel.getName();
				String passwordLogin = gamblerLoginPanel.getPassword();	
				
				if(usernameLogin.isEmpty() || passwordLogin.isEmpty())
					gamblerLoginPanel.showMessage("Login error,\nusername or password is empty!!", MessageColor.Red);
				else{
					MessageGambler messageLogin = new MessageGambler(GamblerCommand.Login, usernameLogin, passwordLogin);
					client.SendGamblerMessage(messageLogin);
					gamblerLoginPanel.clearPanel();
				//	requestRaceList();
				}			
				break;

			case Bet:
				int gamblerBet = gamblerMainPanel.getBet();
				int raceNum[] = new int[] {gamblerMainPanel.getRaceNumber()};
				String carName[] = new String[] {gamblerMainPanel.getCarName()};
				
				MessageGambler messageBet = new MessageGambler(GamblerCommand.Bet, 
						client.getCurrentGambler().getId(), raceNum, carName, gamblerBet );
				client.SendGamblerMessage(messageBet);
				gamblerMainPanel.clearPanel();
				break;
				
			case Cancel:
				mainPane.getChildren().remove(gamblerRegistrationPanel);
				mainPane.getChildren().add(gamblerLoginPanel);
				gamblerRegistrationPanel.clearPanel();
				break;
				
			case goToRegistration:	
				mainPane.getChildren().remove(gamblerLoginPanel);
				mainPane.getChildren().add(gamblerRegistrationPanel);
				gamblerLoginPanel.clearPanel();
				break;
				
			case Exit:
				mainPane.getChildren().remove(gamblerMainPanel);
				mainPane.getChildren().add(gamblerLoginPanel);
				MessageGambler messageLogout = new MessageGambler(GamblerCommand.Logout, 
						client.getCurrentGambler().getName(), client.getCurrentGambler().getPassword());
				client.SendGamblerMessage(messageLogout);			
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * Send request for races list from server.
	 */
	private void requestRaceList() {
		MessageGambler messageGetRaces = new MessageGambler(GamblerCommand.getRaces, true);
		client.SendGamblerMessage(messageGetRaces);
	}
	
	/**
	 * Returns the gambler login panel.
	 * @return the gambler login panel.
	 */
	public GamblerLogin getGamblerLoginPanel() {
		return gamblerLoginPanel;
	}

	/**
	 * Sets the gambler login panel.
	 * @param gamblerLoginPanel	the new gambler login panel.
	 */
	public void setGamblerLoginPanel(GamblerLogin gamblerLoginPanel) {
		this.gamblerLoginPanel = gamblerLoginPanel;
	}

	/**
	 * Returns the gambler registration panel.
	 * @return the gambler registration panel
	 */
	public GamblerRegister getGamblerRegistrationPanel() {
		return gamblerRegistrationPanel;
	}

	/**
	 * Returns the gambler view's main pane.
	 * @return the gambler view's main pane (anchorpane).
	 */
	public AnchorPane getMainPane() {
		return mainPane;
	}

	/**
	 * Sets the main pane to the given new anchor pane.
	 * @param mainPane the new anchor pane.
	 */
	public void setMainPane(AnchorPane mainPane) {
		this.mainPane = mainPane;
	}

	/**
	 * Sets the gambler registration panel to the given new panel.
	 * @param gamblerRegistrationPanel the new gambler registration panel.
	 */
	public void setGamblerRegistrationPanel(GamblerRegister gamblerRegistrationPanel) {
		this.gamblerRegistrationPanel = gamblerRegistrationPanel;
	}

	/**
	 * Returns the gambler main panel.
	 * @return the gambler main panel.
	 */
	public GamblerMainWin getGamblerMainPanel() {
		return gamblerMainPanel;
	}
	
	/**
	 * Sets the gambler main panel to the given new panel.
	 * @param gamblerMainPanel the new gambler main panel.
	 */
	public void setGamblerMainPanel(GamblerMainWin gamblerMainPanel) {
		this.gamblerMainPanel = gamblerMainPanel;
	}


	/**
	 * Event the gambler client uses to notify the view of registration success / failure.
	 * @param success indicates whether registration was successful or not.
	 */
	@Override
	public void registerSuccess(boolean success) {
		if (success) {
			Platform.runLater(() -> {		
				mainPane.getChildren().remove(gamblerRegistrationPanel);
				mainPane.getChildren().add(gamblerLoginPanel);
				gamblerLoginPanel.showMessage("Registration Successful!", MessageColor.Green);
			});
		}
		else{
			Platform.runLater(() -> {
			gamblerRegistrationPanel.showMessage("Error! Registration unsuccessful!", MessageColor.Red);
			});
		}
		
	}
	
	/**
	 * Event the gambler client uses to notify the view of login success / failure.
	 * @param gambler indicates whether login was successful or not and holds information if the login was indeed successful.
	 */
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
			Platform.runLater(() -> {	
			gamblerLoginPanel.showMessage("Error! Login unsuccessful!", MessageColor.Red);
			});
		}
		

	}

	/**
	 * Event the gambler client uses to notify the view of bet success / failure.
	 * @param newBalance indicates the gambler's new balance (after bet deduct).
	 * @param success indicates whether bet was successful or not.
	 */
	@Override
	public void betPlaceSuccess(int newBalance, boolean success) {
		if (success) {
			Platform.runLater(() -> {
				gamblerMainPanel.showGamblerInfo(gamblerMainPanel.getGambler().getName(), newBalance);
				gamblerMainPanel.showMessage("Bet placed successful! Yor current balance: " + newBalance,
						MessageColor.Green);
			});
		} else {
			Platform.runLater(() -> {
				gamblerMainPanel.showMessage("Error! Bet unsuccessful!", MessageColor.Red);
			});
		}
	}

	/**
	 * Event the gambler client uses to notify the view of a new race that started (so that the gambler can bet on).
	 * TODO: update the parameters in the function and in this comment.
	 */
	@Override
	public void newRaceStart() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Event the gambler client uses to notify the view of a race that ended (so the gambler will not be able to bet on it anymore).
	 * TODO: update the parameters in the function and in this comment.
	 */
	@Override
	public void raceEnded() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Display message on current Panel in main Pane.
	 * @param message
	 * @param color
	 */
	public void showMessageOnCurrentPanel(String message, MessageColor color) {
		((IGamblerPanelMessage) mainPane.getChildren().get(0)).showMessage(message, color);;
	}

}
