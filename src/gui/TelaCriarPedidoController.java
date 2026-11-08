package gui;

import beans.Cliente;
import beans.Eletronico;
import beans.Fornecedor;
import beans.Item;
import beans.Moeda;
import beans.Pedido;
import beans.Produto;
import beans.Vestuario;
import negocio.IGerenciadorPedido;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import javafx.util.StringConverter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.io.File;
import java.net.URL;


public class TelaCriarPedidoController {

    @FXML private TextField txtId;
    @FXML private TextField txtStatus;
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Fornecedor> cbFornecedor;
    @FXML private ComboBox<Produto> cbProduto;
    @FXML private ListView<Item> listViewItens;
    @FXML private Label lblMensagem;

    @FXML private TextField txtQuantidade;

    private IGerenciadorPedido gerenciadorPedido;
    private ArrayList<Item> itensTemporarios = new ArrayList<>();

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        this.gerenciadorPedido = gerenciadorPedido;
    }

    @FXML
    public void initialize() {
        //dados mock p/ teste
        Cliente clienteFalso = new Cliente(1L, "Fulano de Tal", 12345678911L, "Rua Exemplo, 123, Cidade, Estado");
        cbCliente.getItems().add(clienteFalso);
        
        Fornecedor fornecedorFalso = new Fornecedor(1L, "Tech Store", "China", Moeda.CNY); 
        cbFornecedor.getItems().add(fornecedorFalso);
        
        Produto prod1 = new Eletronico(101L, "Smartphone", 1500.0, 0.5, 60.0);
        Produto prod2 = new Vestuario(102L, "Camisa de Futebol Tamanho G", 150.0, 0.3);
        cbProduto.getItems().addAll(prod1, prod2);

        cbCliente.getSelectionModel().selectFirst();
        cbFornecedor.getSelectionModel().selectFirst();
        cbProduto.getSelectionModel().selectFirst();
        //fim dos mock

        //Conversores para limpar as caixas (mostram apenas o nome)
        cbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return (cliente == null) ? "" : cliente.getNome();
            }
            @Override
            public Cliente fromString(String string) { return null; }
        });

        cbFornecedor.setConverter(new StringConverter<Fornecedor>() {
            @Override
            public String toString(Fornecedor fornecedor) {
                return (fornecedor == null) ? "" : fornecedor.getNome();
            }
            @Override
            public Fornecedor fromString(String string) { return null; }
        });

        cbProduto.setConverter(new StringConverter<Produto>() {
            @Override
            public String toString(Produto produto) {
                return (produto == null) ? "" : produto.getNome();
            }
            @Override
            public Produto fromString(String string) { return null; }
        });
    }
    
    @FXML
    private void handleAdicionarItem(ActionEvent event) {
        try {
            Produto produtoSelecionado = cbProduto.getValue();
            if (produtoSelecionado == null) {
                throw new Exception("Selecione um produto na lista.");
            }
            
            int qtd = Integer.parseInt(txtQuantidade.getText());
            
            int pesoCalculado = (int) (produtoSelecionado.getPeso() * qtd);

            Item novoItem = new Item(produtoSelecionado.getNome(), qtd, pesoCalculado, 0.0, "R$"); 
            
            itensTemporarios.add(novoItem);
            listViewItens.getItems().add(novoItem);
       
            txtQuantidade.clear();
            cbProduto.getSelectionModel().clearSelection();
            
            lblMensagem.setText("Item adicionado com sucesso.");
            lblMensagem.setTextFill(javafx.scene.paint.Color.GREEN);
            
        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro: Quantidade deve ser um número inteiro.");
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
            
            //REQ23
            if (itensTemporarios.isEmpty()) {
                throw new Exception("O pedido deve conter pelo menos um item.");
            }

            for (Item item : itensTemporarios) {
                novoPedido.adicionar(item);
            }

            //Salva o pedido no sistema
            gerenciadorPedido.criarPedido(novoPedido);

            //Redireciona para a tela de atualização de status
            URL url = new File("src/gui/TelaAtualizarStatusPedido.fxml").toURI().toURL(); 
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            
            TelaAtualizarStatusController controller = loader.getController();
            controller.setGerenciadorPedido(this.gerenciadorPedido); 

            // Pega a janela atual a partir do clique no botão e muda a cena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("ImportFX - Atualizar Status");
            stage.setScene(new Scene(root));

        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro: O ID deve ser um número válido.");
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            lblMensagem.setText("Erro: " + e.getMessage());
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

}