package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestaTelaCliente extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega a tela FXML de Fornecedores em vez da de Clientes
        Parent root = FXMLLoader.load(getClass().getResource("/gui/TelaFornecedor.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("ImportFX - Teste de Cadastro de Fornecedores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}