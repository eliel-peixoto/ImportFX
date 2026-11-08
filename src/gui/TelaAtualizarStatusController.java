package gui;

import beans.Pedido;
import negocio.IGerenciadorPedido;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.awt.Desktop;
import java.io.File;

public class TelaAtualizarStatusController {

    @FXML private TextField txtIdBusca;
    @FXML private Label lblStatusAtual;
    @FXML private ComboBox<String> cbNovoStatus;
    @FXML private Label lblMensagemStatus;

    private IGerenciadorPedido gerenciadorPedido;
    private Pedido pedidoSelecionado; 

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        this.gerenciadorPedido = gerenciadorPedido;
    }

    @FXML
    public void initialize() {
        cbNovoStatus.getItems().addAll(
            "Pendente",
            "Aguardando Pagamento",
            "Pago",
            "Em Trânsito",
            "Alfândega",
            "Entregue"
        );
    }

    @FXML
    private void handleBuscarPedido(ActionEvent event) {
        try {
            Long id = Long.parseLong(txtIdBusca.getText());
            pedidoSelecionado = gerenciadorPedido.buscarPedido(id); 

            if (pedidoSelecionado != null) {
                lblStatusAtual.setText(pedidoSelecionado.getStatus());
                lblMensagemStatus.setText("Pedido encontrado!");
                lblMensagemStatus.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                lblStatusAtual.setText("---");
                lblMensagemStatus.setText("Erro: Pedido não encontrado.");
                lblMensagemStatus.setTextFill(javafx.scene.paint.Color.RED);
            }
        } catch (NumberFormatException e) {
            lblMensagemStatus.setText("Erro: Digite um ID numérico válido.");
            lblMensagemStatus.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void handleAtualizarStatus(ActionEvent event) {
        if (pedidoSelecionado == null) {
            lblMensagemStatus.setText("Aviso: Busque um pedido primeiro.");
            lblMensagemStatus.setTextFill(javafx.scene.paint.Color.ORANGE);
            return;
        }

        String novoStatus = cbNovoStatus.getValue();
        if (novoStatus == null) {
            lblMensagemStatus.setText("Aviso: Selecione um novo status.");
            lblMensagemStatus.setTextFill(javafx.scene.paint.Color.ORANGE);
            return;
        }

        //Atualiza o objeto na memória
        pedidoSelecionado.setStatus(novoStatus);
        
        //Atualiza a tela
        lblStatusAtual.setText(novoStatus);
        lblMensagemStatus.setText("Status atualizado para: " + novoStatus);
        lblMensagemStatus.setTextFill(javafx.scene.paint.Color.GREEN);
        
        //Opcional: Limpar a caixa
        cbNovoStatus.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleGerarHtml(ActionEvent event) {
        if (pedidoSelecionado == null) {
            lblMensagemStatus.setText("Aviso: Busque um pedido primeiro para gerar o HTML.");
            lblMensagemStatus.setTextFill(javafx.scene.paint.Color.ORANGE);
            return;
        }

        try {
            //1. Gera o HTML na raiz do projeto
            String pastaDestino = System.getProperty("user.dir"); 
            gerenciadorPedido.exportarPedidoParaHtml(pedidoSelecionado.getId(), pastaDestino);
            
            //2. Localiza o ficheiro gerado. 
            
            File arquivoHtml = new File(pastaDestino, "pedido_" + pedidoSelecionado.getId() + ".html");
            
            //3. Abre automaticamente no navegador padrão do sistema
            if (arquivoHtml.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(arquivoHtml.toURI());
                lblMensagemStatus.setText("HTML gerado e aberto com sucesso!");
                lblMensagemStatus.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                lblMensagemStatus.setText("HTML gerado, mas abra-o manualmente na pasta do projeto.");
                lblMensagemStatus.setTextFill(javafx.scene.paint.Color.GREEN);
            }
            
        } catch (Exception e) {
            lblMensagemStatus.setText("Erro ao exportar HTML: " + e.getMessage());
            lblMensagemStatus.setTextFill(javafx.scene.paint.Color.RED);
        }
    }
}