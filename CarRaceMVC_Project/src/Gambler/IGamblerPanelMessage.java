package Gambler;

enum MessageColor {Green, Red}

public interface IGamblerPanelMessage {
	public void showMessage(String message, MessageColor color);
}
