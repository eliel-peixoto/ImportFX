package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource("/gui/TelaLogin.fxml"));
        primaryStage.setTitle("ImportFX - Acesso ao Sistema");
        primaryStage.setScene(new Scene(raiz));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}