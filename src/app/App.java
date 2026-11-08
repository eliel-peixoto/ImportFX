package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    /*@Override
    public void start(Stage primaryStage) throws Exception {
        // O caminho abaixo é a tela padrão de início:
        Parent root = FXMLLoader.load(getClass().getResource("/gui/TelaCliente.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("ImportFX - Cadastro de Clientes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/

     @Override
    public void start(Stage primaryStage) throws Exception {
        // Tela inicial do programa: o menu principal
        Parent root = FXMLLoader.load(getClass().getResource("/gui/MenuPrincipal.fxml"));
 
        primaryStage.setTitle("ImportFX - Menu Principal");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
