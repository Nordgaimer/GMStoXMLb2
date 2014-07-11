package FXMLControllers;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @web http://java-buddy.blogspot.com/
 */
public class FileSaver extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("java-buddy.blogspot.com");
        Group root = new Group();

        final String Santa_Claus_Is_Coming_To_Town =
                "You better watch out\n"
                        + "You better not cry\n"
                        + "Better not pout\n"
                        + "I'm telling you why\n"
                        + "Santa Claus is coming to town\n"
                        + "\n"
                        + "He's making a list\n"
                        + "And checking it twice;\n"
                        + "Gonna find out Who's naughty and nice\n"
                        + "Santa Claus is coming to town\n"
                        + "\n"
                        + "He sees you when you're sleeping\n"
                        + "He knows when you're awake\n"
                        + "He knows if you've been bad or good\n"
                        + "So be good for goodness sake!\n"
                        + "\n"
                        + "O! You better watch out!\n"
                        + "You better not cry\n"
                        + "Better not pout\n"
                        + "I'm telling you why\n"
                        + "Santa Claus is coming to town\n"
                        + "Santa Claus is coming to town\n";

        Text textSong = TextBuilder.create()
                .text(Santa_Claus_Is_Coming_To_Town)
                .build();

        Button buttonSave = ButtonBuilder.create()
                .text("Save")
                .build();

        buttonSave.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(primaryStage);

                if(file != null){
                    SaveFile(Santa_Claus_Is_Coming_To_Town, file);
                }
            }
        });

        VBox vBox = VBoxBuilder.create()
                .children(textSong, buttonSave)
                .build();

        root.getChildren().add(vBox);

        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();

    }

    private void SaveFile(String content, File file){
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(FileSaver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}