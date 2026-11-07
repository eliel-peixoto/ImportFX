package gui;

import beans.Cliente;
import beans.Fornecedor;
import beans.Item;
import beans.Pedido;
import negocio.IGerenciadorPedido;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.util.ArrayList;

public class TelaCriarPedidoController {

    @FXML private TextField txtId;
    @FXML private TextField txtStatus;
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Fornecedor> cbFornecedor;
    @FXML private ListView<Item> listViewItens;
    @FXML private Label lblMensagem;

    //Novos campos para capturar os dados do Item
    @FXML private TextField txtNomeProduto;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtPesoTotal;

    private IGerenciadorPedido gerenciadorPedido;
    private ArrayList<Item> itensTemporarios = new ArrayList<>();

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        this.gerenciadorPedido = gerenciadorPedido;
    }

    @FXML
    public void initialize() {
        //Carregamento de clientes e fornecedores viria aqui
    }

    @FXML
    private void handleAdicionarItem(ActionEvent event) {
        try {
            String nome = txtNomeProduto.getText();
            if (nome == null || nome.trim().isEmpty()) {
                throw new Exception("O nome do produto é obrigatório.");
            }
            
            //Converte os textos para inteiros conforme pede a sua classe Item
            int qtd = Integer.parseInt(txtQuantidade.getText());
            int peso = Integer.parseInt(txtPesoTotal.getText());

            //Cria o objeto Item com os dados reais
            Item novoItem = new Item(nome, qtd, peso, 0.0, "placeholder"); 
            
            itensTemporarios.add(novoItem);
            listViewItens.getItems().add(novoItem);
            
            //Limpa os campos do item após adicionar
            txtNomeProduto.clear();
            txtQuantidade.clear();
            txtPesoTotal.clear();
            
            lblMensagem.setText("Item adicionado com sucesso.");
            lblMensagem.setTextFill(javafx.scene.paint.Color.GREEN);
            
        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro: Quantidade e Peso devem ser números inteiros.");
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            lblMensagem.setText("Erro: " + e.getMessage());
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void handleSalvarPedido(ActionEvent event) {
        try {
            Long id = Long.parseLong(txtId.getText());
            String status = txtStatus.getText();
            Cliente clienteSelecionado = cbCliente.getValue();
            Fornecedor fornecedorSelecionado = cbFornecedor.getValue();

            Pedido novoPedido = new Pedido(clienteSelecionado, fornecedorSelecionado, id, status);
            
            for (Item item : itensTemporarios) {
                novoPedido.adicionar(item);
            }

            gerenciadorPedido.criarPedido(novoPedido);
            //Tenta exportar automaticamente após criar o pedido
            try {
            //Passa o ID do pedido e a pasta atual (".") como destino
                gerenciadorPedido.exportarPedidoParaHtml(novoPedido.getId(), ".");
                lblMensagem.setText("Pedido salvo e HTML gerado com sucesso!");
            } 
            catch (Exception ex) {
                lblMensagem.setText("Pedido salvo, mas erro ao gerar HTML: " + ex.getMessage());
            }
            lblMensagem.setText("Pedido cadastrado com sucesso!");
            lblMensagem.setTextFill(javafx.scene.paint.Color.GREEN);
            limparFormulario();

        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro: O ID deve ser um número válido.");
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            lblMensagem.setText("Erro: " + e.getMessage());
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private void limparFormulario() {
        txtId.clear();
        cbCliente.getSelectionModel().clearSelection();
        cbFornecedor.getSelectionModel().clearSelection();
        listViewItens.getItems().clear();
        itensTemporarios.clear();
    }
}