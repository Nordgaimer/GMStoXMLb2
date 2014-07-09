import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;


    public class Main extends Application {
        @Override public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("getConnection.fxml"));
            Scene scene = new Scene(root,300,190);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("GMS to XML Beta 0.2");
            primaryStage.show();
        }
        /**
         * The main() method is ignored in correctly deployed JavaFX application.
         * main() serves only as fallback in case the application can not be
         * launched through deployment artifacts, e.g., in IDEs with limited FX
         * support. NetBeans ignores main().
         *
         * @param args the command line arguments
         */
        public static void main(String[] args) {
            launch(args);
        }

    }

