package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Aponta para o ficheiro FXML (O caminho deve bater certo com a sua pasta resources)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaCriarPedido.fxml"));
            Parent root = loader.load();

            // 2. (Opcional) Se precisar injetar os repositórios/gerenciadores no Controller antes da tela abrir:
            /*
            TelaCriarPedidoController controller = loader.getController();
            controller.setControlPedido(seuGerenciadorDePedidos);
            controller.setControlCambio(seuGerenciadorDeCambio);
            */

            // 3. Monta a janela e exibe
            Scene scene = new Scene(root);
            primaryStage.setTitle("ImportFX - Módulo de Pedidos");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Impede de maximizar e desalinhar os botões
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar a tela: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Dispara a aplicação JavaFX
        launch(args);
    }
}