package gui;

import dados.ClientePtax;
import dados.Repositorios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import negocio.GerenciadorCambio;
import negocio.GerenciadorPedido;
import negocio.GerenciadorRelatorios;

public class MenuPrincipalController {

    @FXML
    private void handleAbrirProduto(ActionEvent event) {
        trocarTela(event, "/gui/TelaCadProduto.fxml", "ImportFX - Cadastro de Produtos");
    }

    @FXML
    private void handleAbrirPedido(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaCriarPedido.fxml"));
            Parent raiz = loader.load();
            TelaCriarPedidoController controller = loader.getController();
            controller.setGerenciadorPedido(new GerenciadorPedido(Repositorios.pedidos()));
            controller.setGerenciadorCambio(new GerenciadorCambio(
                    new ClientePtax(), Repositorios.cotacoes()));
            controller.setGerenciadorRelatorios(new GerenciadorRelatorios(Repositorios.pedidos()));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("ImportFX - Novo Pedido");
            stage.setScene(new Scene(raiz));
            stage.show();
        } catch (Exception erro) {
            mostrarErro("Não foi possível abrir a tela de pedidos: " + erro.getMessage());
        }
    }

    @FXML
    private void handleAbrirCadastro(ActionEvent event) {
        mostrarAviso("A tela de cadastro ainda não foi conectada ao menu.");
    }

    @FXML
    private void handleAbrirRelatorio(ActionEvent event) {
        try {
            Stage janelaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            JanelaRelatorio.abrir(janelaAtual,
                    new GerenciadorRelatorios(Repositorios.pedidos()));
        } catch (Exception erro) {
            mostrarErro("Não foi possível abrir os relatórios: " + erro.getMessage());
        }
    }

    @FXML
    private void handleAbrirCambio(ActionEvent event) {
        mostrarAviso("A tela de câmbio ainda não foi conectada ao menu.");
    }

    private void trocarTela(ActionEvent event, String caminho, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminho));
            Parent raiz = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(raiz));
            stage.show();
        } catch (Exception erro) {
            mostrarErro("Não foi possível abrir a tela: " + erro.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem);
        alerta.setHeaderText("Erro");
        alerta.showAndWait();
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
