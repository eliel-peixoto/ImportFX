package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {

   @Override
    public void start(Stage primaryStage) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource("/gui/MenuPrincipal.fxml"));
        primaryStage.setTitle("ImportFX - Menu Principal");
        primaryStage.setScene(new Scene(raiz));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
