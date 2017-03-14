package Gambler;

import javafx.event.EventHandler;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GamblerButton extends ImageView {

	public enum ButtonId {
		goToLogin, Login, goToRegistration, Registration, Bet, Back, Cancel, Exit
	};

	private DropShadow shadowEffect = new DropShadow();
	public ButtonId btnId;

	public GamblerButton(ButtonId btnId, String buttonName, int imgWidth, int imgHeight, double buttonWidth, double buttonHeight) {

		this.btnId = btnId;
		setImage(new Image( GamblerButton.class.getClass().getResource("/Gambler/resources/" + buttonName).toExternalForm(), imgWidth, imgHeight, true, true));
		setFitWidth(buttonWidth);
		setFitHeight(buttonHeight);
		shadowEffect.setColor(Color.WHITE);

		addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				setEffect(shadowEffect);
			}
		});

		addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				setEffect(null);
			}
		});
	}

	public ButtonId getButtonId() {
		return btnId;
	}
}
