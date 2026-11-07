package gui;

import beans.Pedido;
import negocio.IGerenciadorPedido; // Ajuste se o pacote/nome for diferente
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TelaAtualizarStatusPedidoController {

    @FXML private TextField txtIdBusca;
    @FXML private Label lblStatusAtual;
    @FXML private ComboBox<String> cbNovoStatus;
    @FXML private Label lblMensagemStatus;

    private IGerenciadorPedido controlPedido;
    private Pedido pedidoSelecionado; //Guarda o pedido encontrado na busca

    @FXML
    public void initialize() {
        cbNovoStatus.getItems().addAll(
            "Aguardando Pagamento",
            "Pago",
            "Em Trânsito",
            "Alfândega",
            "Entregue"
        );
    }

    //Método para injetar o gerenciador a partir da sua classe principal (Main)
    public void setControlPedido(IGerenciadorPedido controlPedido) {
        this.controlPedido = controlPedido;
    }

    //Ação do botão "Buscar"
    @FXML
    public void handleBuscarPedido() {
        lblMensagemStatus.setText("");
        
        if (txtIdBusca.getText().trim().isEmpty()) {
            lblStatusAtual.setText("---");
            pedidoSelecionado = null;
            mostrarAlerta("Aviso", "Digite o ID do pedido para buscar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            long id = Long.parseLong(txtIdBusca.getText().trim());
            
            pedidoSelecionado = controlPedido.buscarPedido(id); 

            if (pedidoSelecionado == null) {
                lblStatusAtual.setText("---");
                mostrarAlerta("Erro", "Pedido não encontrado no sistema.", Alert.AlertType.ERROR);
            } else {
                lblStatusAtual.setText(pedidoSelecionado.getStatus());
            }
        } catch (NumberFormatException e) {
            lblStatusAtual.setText("---");
            pedidoSelecionado = null;
            mostrarAlerta("Erro", "O ID deve ser um número inteiro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAtualizarStatus() {
        lblMensagemStatus.setText("");

        if (pedidoSelecionado == null) {
            mostrarAlerta("Aviso", "Por favor, busque um pedido antes de tentar atualizar.", Alert.AlertType.WARNING);
            return;
        }

        String novoStatus = cbNovoStatus.getValue();
        if (novoStatus == null) {
            mostrarAlerta("Aviso", "Selecione o novo status na lista.", Alert.AlertType.WARNING);
            return;
        }

        pedidoSelecionado.setStatus(novoStatus);
        
        /* 
         * NOTA: Se o seu repositório precisar de um método explícito para atualizar (ex: controlPedido.atualizar(pedido)),
         * coloque essa chamada aqui. Se for apenas guardar na memória, o setStatus acima basta.
         */
        
        // REQ15: Notificar na tela que o status mudou!
        mostrarAlerta("Notificação ao Cliente", 
            "O status do Pedido #" + pedidoSelecionado.getId() + " foi alterado para:\n" + novoStatus, 
            Alert.AlertType.INFORMATION);
        
        // Limpa a tela para a próxima busca
        txtIdBusca.clear();
        lblStatusAtual.setText("---");
        cbNovoStatus.setValue(null);
        pedidoSelecionado = null;
    }

    // Método auxiliar para criar janelas de aviso/erro no JavaFX
    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}