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
import javafx.scene.text.Font;


public class GamblerTextField extends GridPane {

	private final int fieldTypeText = 0;
	private Control textField;

	public GamblerTextField(String text, int type) {

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(35);
		column1.setFillWidth(true);

		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(65);
		column2.setFillWidth(true);

		getColumnConstraints().add(column1);
		getColumnConstraints().add(column2);

		Label textLbl = new Label(text);
		textLbl.setTextFill(Color.DARKBLUE);
		textLbl.setStyle("-fx-font-weight: bold;");
		textLbl.setFont(new Font("Serif", 22));

		if (type == fieldTypeText) 
			textField = new TextField();
		else
			textField = new PasswordField();

		textField.setBackground(new Background(new BackgroundFill(Color.web("#ff000000"), CornerRadii.EMPTY, Insets.EMPTY)));
		textField.setStyle("-fx-text-inner-color: white;");
		textField.setStyle("-fx-background-color: rgba(255,255,255, 0.5)");

		add(textLbl, 0, 0);
		add(textField, 1, 0);
	}

	public Control getTextControl() {
		return textField;
	}
}

class GamblerComboBox extends GridPane {

	private ObservableList<String> options;
	private ComboBox<String> comboBox;

	public GamblerComboBox(String text) {
		options =  FXCollections.observableArrayList();
		comboBox = new ComboBox<String>(options);
				 			 
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

	public Control getComboControl() {
		return comboBox;
	}
	
	public ObservableList<String> getOptionsList() {
		return options;
	}
	
	public void setOptionsList(ObservableList<String> options) {
		this.options = options;
		comboBox.setItems(this.options);
	}
		
	public String getSelectedOption() {
		return comboBox.getValue();
	}
	
	public void clearComboBox(){
		
	}
}
