package ConnectionManager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ErrorMsg extends Stage {
    public ErrorMsg(String title, String resultText, boolean isSuccess){
        if (isSuccess){
            Group root = new Group();
            Scene scene = new Scene(root, 300,30);
            HBox vBox_frame = new HBox(10);
            TextArea  textArea = new TextArea();
            textArea.setText(resultText);
            vBox_frame.getChildren().addAll(textArea);
            root.getChildren().add(vBox_frame);
            centerOnScreen();
            setResizable(false);
            setTitle("Error#03");
            setScene(scene);
            show();
        }
        else {
            Group root = new Group();
            Scene scene = new Scene(root, 300,300);
            HBox vBox_frame = new HBox(10);
            Label textField = new Label();
            textField.setText(resultText);
            vBox_frame.getChildren().addAll(textField);
            root.getChildren().add(vBox_frame);
            centerOnScreen();
            setResizable(false);
            setTitle("Error#02");
            setScene(scene);
            show();
        }
    }

    public ErrorMsg(String title){
            Group root = new Group();
            Scene scene = new Scene(root, 250,100);
            HBox vBox_frame = new HBox(10);
            TextArea  textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setText(title);
            vBox_frame.getChildren().addAll(textArea);
            root.getChildren().add(vBox_frame);
            centerOnScreen();
            setResizable(false);
            setTitle("Error#01");
            setScene(scene);
            show();
        }
}
