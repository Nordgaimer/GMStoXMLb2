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


public final class ErrorMsge {

    public ErrorMsge(String title) {
        Stage stage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root, 250, 100);
        HBox vBox_frame = new HBox(10);
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setText(title);
        textArea.setPrefSize(250,100);
        vBox_frame.getChildren().addAll(textArea);
        stage.setScene(scene);
        root.getChildren().add(vBox_frame);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Ошибка");
        stage.show();
    }
}
