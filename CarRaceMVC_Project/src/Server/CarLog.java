package Server;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CarLog {
	private ScrollPane srcPane;
	private VBox vBoxInSrcPane;
	private BorderPane mainFrame;

	public CarLog() {
		srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		vBoxInSrcPane = new VBox(3);
		mainFrame = new BorderPane();
		srcPane.setContent(vBoxInSrcPane);
		mainFrame.setCenter(srcPane);
	}

	public void printMsg(String str) {
		Label action = new Label(str);
		vBoxInSrcPane.getChildren().add(action);
		srcPane.setVvalue(action.getScaleY() + action.getHeight());
	}
}