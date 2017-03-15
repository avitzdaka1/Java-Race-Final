package Gambler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;


public class GamblerTextField extends GridPane {

	private final int fieldTypeText = 0;
	private Control textControl;

	public GamblerTextField(String text, int type) {

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(35);
		column1.setFillWidth(true);

		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(65);
		column2.setFillWidth(true);

		getColumnConstraints().add(column1);
		getColumnConstraints().add(column2);

		Label textLB = new Label(text);
		textLB.setTextFill(Color.DARKBLUE);
		textLB.setStyle("-fx-font-weight: bold;");

		if (type == fieldTypeText) 
			textControl = new TextField();
		else
			textControl = new PasswordField();

		textControl.setBackground(new Background(new BackgroundFill(Color.web("#ff000000"), CornerRadii.EMPTY, Insets.EMPTY)));
		textControl.setStyle("-fx-text-inner-color: white;");
		textControl.setStyle("-fx-background-color: rgba(255,255,255, 0.5)");

		add(textLB, 0, 0);
		add(textControl, 1, 0);
	}

	public Control getTextControl() {
		return textControl;
	}
}

class GamblerComboBox extends GridPane {

	private ObservableList<String> options;
	private Control comboBox;

	public GamblerComboBox(String text) {
		options =  FXCollections.observableArrayList();
		ComboBox comboBox = new ComboBox(options);
				 			 
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(35);
		column1.setFillWidth(true);

		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(65);
		column2.setFillWidth(true);

		getColumnConstraints().add(column1);
		getColumnConstraints().add(column2);

		Label textLB = new Label(text);
		textLB.setTextFill(Color.DARKBLUE);
		textLB.setStyle("-fx-font-weight: bold;");

		comboBox.setBackground(new Background(new BackgroundFill(Color.web("#ff000000"), CornerRadii.EMPTY, Insets.EMPTY)));
		comboBox.setStyle("-fx-text-inner-color: white;");
		comboBox.setStyle("-fx-background-color: rgba(255,255,255, 0.5)");
		comboBox.setPrefSize(240, 30);

		add(textLB, 0, 0);
		add(comboBox, 1, 0);
	}

	public Control getTextControl() {
		return comboBox;
	}
	
	public ObservableList<String> getOptionsList() {
		return options;
	}
}
