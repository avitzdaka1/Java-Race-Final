package Server;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CarLog extends BorderPane {
	private ScrollPane srcPane;
	private VBox vBoxInSrcPane;

	public CarLog() {
		srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		vBoxInSrcPane = new VBox(3);
		srcPane.setContent(vBoxInSrcPane);
		setCenter(srcPane);
	}

	public void printMsg(String str) {
		Label action = new Label(str);
		vBoxInSrcPane.getChildren().add(action);
		srcPane.setVvalue(action.getScaleY() + action.getHeight());
	}
	
	public void clearLog() {
		vBoxInSrcPane.getChildren().clear();
	}
}