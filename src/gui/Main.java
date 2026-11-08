package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

public class Main extends Application {

   @Override
    public void start(Stage primaryStage) {
        try {
            //1. Cria a inteligência do negócio (vazia no início)
            dados.RepositorioPedidos meuRepositorio = new dados.RepositorioPedidos(); 
            negocio.GerenciadorPedido meuGerenciador = new negocio.GerenciadorPedido(meuRepositorio);

            //2. Carrega a tela de Criar Pedido
            URL url = new File("src/gui/TelaCriarPedido.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            
            //3. Injeta o gerenciador na tela de criação
            TelaCriarPedidoController controller = loader.getController();
            controller.setGerenciadorPedido(meuGerenciador); 

            Scene scene = new Scene(root);
            primaryStage.setTitle("ImportFX - Novo Pedido");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); 
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar a tela: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}