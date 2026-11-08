package gui;
 
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
 
import java.net.URL;
 
public class MenuPrincipalController {
 
    @FXML
    private void handleAbrirProduto(ActionEvent event) {
        trocarTela(event, "/gui/TelaCadProduto.fxml", "ImportFX - Cadastro de Produtos");
    }
 
    @FXML
    private void handleAbrirPedido(ActionEvent event) {
        // Troque pelo nome real do FXML quando a tela de pedido existir
        trocarTela(event, "/gui/CadastroPedidoView.fxml", "ImportFX - Pedidos");
    }
 
    @FXML
    private void handleAbrirCadastro(ActionEvent event) {
        emBreve("Cadastro");
    }
 
    @FXML
    private void handleAbrirRelatorio(ActionEvent event) {
        emBreve("Relatório");
    }
 
    @FXML
    private void handleAbrirCambio(ActionEvent event) {
        emBreve("Câmbio");
    }
 
    private void trocarTela(ActionEvent event, String fxmlPath, String titulo) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                emBreve(titulo);
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alerta = new Alert(Alert.AlertType.ERROR, "Não foi possível abrir a tela: " + e.getMessage());
            alerta.showAndWait();
        }
    }
 
    private void emBreve(String tela) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, "A tela \"" + tela + "\" ainda não foi implementada.");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
