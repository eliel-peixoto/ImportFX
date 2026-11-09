package gui;

import beans.Cliente;
import beans.Cotacao;
import beans.Eletronico;
import beans.Fornecedor;
import beans.Item;
import beans.Moeda;
import beans.Pedido;
import beans.Produto;
import beans.Vestuario;
import negocio.IGerenciadorPedido;
import negocio.IGerenciadorCambio;
import negocio.IGerenciadorRelatorios;
import negocio.GerenciadorRelatorios;
import dados.Repositorios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.ArrayList;
import javafx.util.StringConverter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.util.Optional;


public class TelaCriarPedidoController {

    @FXML private TextField txtId;
    @FXML private TextField txtStatus;
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Fornecedor> cbFornecedor;
    @FXML private ComboBox<Produto> cbProduto;
    @FXML private ListView<Item> listViewItens;
    @FXML private Label lblMensagem;

    @FXML private TextField txtQuantidade;
    @FXML private Button btnSalvarPedido;

    private IGerenciadorPedido gerenciadorPedido;
    private IGerenciadorCambio gerenciadorCambio;
    private IGerenciadorRelatorios gerenciadorRelatorios =
            new GerenciadorRelatorios(Repositorios.pedidos());
    private ArrayList<Item> itensTemporarios = new ArrayList<>();
    private boolean pedidoSalvo;
    private Cotacao cotacaoDoPedido;

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        this.gerenciadorPedido = gerenciadorPedido;
    }

    public void setGerenciadorCambio(IGerenciadorCambio gerenciadorCambio) {
        this.gerenciadorCambio = gerenciadorCambio;
    }

    public void setGerenciadorRelatorios(IGerenciadorRelatorios gerenciadorRelatorios) {
        if (gerenciadorRelatorios != null) this.gerenciadorRelatorios = gerenciadorRelatorios;
    }

    @FXML
    public void initialize() {
        //dados mock p/ teste
        Cliente clienteFalso = new Cliente(1L, "Fulano de Tal", 12345678911L, "Rua Exemplo, 123, Cidade, Estado");
        cbCliente.getItems().add(clienteFalso);
        
        Fornecedor fornecedorFalso = new Fornecedor(1L, "Tech Store", "Inglaterra", Moeda.EUR); 
        cbFornecedor.getItems().add(fornecedorFalso);
        
        Produto prod1 = new Eletronico(101L, "Smartphone", 1500.0, 0.5, 60.0);
        Produto prod2 = new Vestuario(102L, "Camisa de Futebol Tamanho G", 25.0  , 150.0, 0.3);
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
            if (qtd <= 0) {
                throw new Exception("A quantidade deve ser maior que zero.");
            }
            
            double pesoCalculado = produtoSelecionado.getPeso() * qtd;
            double precoOriginal = produtoSelecionado.getPrecoMoedaOrigem() == null
                    ? 0.0 : produtoSelecionado.getPrecoMoedaOrigem();
            double precoUnitario = precoOriginal;
            Moeda moeda = cbFornecedor.getValue() == null
                    ? null : cbFornecedor.getValue().getMoedaPadrao();
            String simboloMoeda = moeda == Moeda.BRL ? "R$"
                    : moeda == null ? "N/I" : moeda.name();
            String mensagemConversao = "Item adicionado.";
            boolean convertido = moeda == Moeda.BRL;

            if (moeda == null) {
                throw new Exception("Selecione o fornecedor do pedido.");
            }
            if (moeda != Moeda.BRL) {
                if (gerenciadorCambio == null) {
                    throw new Exception("O câmbio não está configurado.");
                }
                if (cotacaoDoPedido != null && cotacaoDoPedido.getMoeda() != moeda) {
                    throw new Exception("A moeda do fornecedor mudou depois do primeiro item. "
                            + "Volte ao menu e crie um novo pedido para trocar de fornecedor.");
                }
                if (cotacaoDoPedido == null) {
                    // Todos os itens usam a MESMA cotação. Sem cotação (nem em cache) o item é recusado
                    cotacaoDoPedido = gerenciadorCambio.obterCotacao(moeda);
                }
                precoUnitario = gerenciadorCambio.converterParaReal(precoOriginal, cotacaoDoPedido);
                simboloMoeda = "R$";
                convertido = true;
                mensagemConversao = "Item adicionado e convertido para reais (1 " + moeda + " = R$ "
                        + String.format("%.4f", cotacaoDoPedido.getVenda()) + ")"
                        + (cotacaoDoPedido.isDesatualizada()
                                ? ". ATENÇÃO: cotação do cache, a API do Banco Central está indisponível."
                                : ".");
            }

            Item novoItem = new Item(produtoSelecionado.getNome(), qtd, pesoCalculado,
                    precoUnitario, simboloMoeda);
            novoItem.setIdProduto(produtoSelecionado.getId());
            novoItem.setCategoria(nomeCategoria(produtoSelecionado));
            novoItem.setAliquota(obterAliquota(produtoSelecionado));
            
            itensTemporarios.add(novoItem);
            listViewItens.getItems().add(novoItem);
       
            txtQuantidade.clear();
            cbProduto.getSelectionModel().clearSelection();
            
            lblMensagem.setText(mensagemConversao);
            lblMensagem.setTextFill(convertido
                    ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.ORANGE);
            
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
            
            if (itensTemporarios.isEmpty()) {
                throw new Exception("O pedido deve conter pelo menos um item.");
            }

            for (Item item : itensTemporarios) {
                novoPedido.adicionar(item);
            }

            if (cotacaoDoPedido != null) {
                novoPedido.congelarCotacao(cotacaoDoPedido);
            }

            //Salva o pedido no sistema
            gerenciadorPedido.criarPedido(novoPedido);
            pedidoSalvo = true;
            btnSalvarPedido.setDisable(true);

            //Redireciona para a tela de atualização de status
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gui/TelaAtualizarStatusPedido.fxml"));
            Parent root = loader.load();

            
            TelaAtualizarStatusController controller = loader.getController();
            controller.setGerenciadorPedido(this.gerenciadorPedido); 
            controller.setGerenciadorRelatorios(this.gerenciadorRelatorios);

            // Abre o status em uma nova janela e mantém o pedido visível atrás dela.
            Stage janelaPedido = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage janelaStatus = new Stage();
            janelaStatus.initOwner(janelaPedido);
            janelaStatus.setTitle("ImportFX - Atualizar Status");
            janelaStatus.setScene(new Scene(root));
            janelaStatus.show();

        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro: O ID deve ser um número válido.");
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            lblMensagem.setText("Erro: " + e.getMessage());
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void handleAbrirRelatorios(ActionEvent event) {
        try {
            JanelaRelatorio.abrir(((Node) event.getSource()).getScene().getWindow(),
                    gerenciadorRelatorios);
        } catch (Exception erro) {
            lblMensagem.setText("Não foi possível abrir os relatórios: " + erro.getMessage());
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void handleVoltarMenu(ActionEvent event) {
        boolean temDadosNaoSalvos = !pedidoSalvo
                && (!itensTemporarios.isEmpty() || !txtId.getText().isBlank());
        if (temDadosNaoSalvos) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Os dados deste pedido ainda não foram salvos. Deseja voltar ao menu e descartá-los?",
                    ButtonType.CANCEL, ButtonType.OK);
            confirmacao.setHeaderText("Descartar pedido?");
            Optional<ButtonType> resposta = confirmacao.showAndWait();
            if (resposta.isEmpty() || resposta.get() != ButtonType.OK) return;
        }

        try {
            Parent raiz = FXMLLoader.load(getClass().getResource("/gui/MenuPrincipal.fxml"));
            Stage janela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janela.setTitle("ImportFX - Menu Principal");
            janela.setScene(new Scene(raiz));
        } catch (Exception erro) {
            lblMensagem.setText("Não foi possível voltar ao menu: " + erro.getMessage());
            lblMensagem.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private String nomeCategoria(Produto produto) {
        if (produto instanceof Eletronico) return "Eletrônico";
        if (produto instanceof Vestuario) return "Vestuário";
        if (produto instanceof beans.Brinquedo) return "Brinquedo";
        return "Não informada";
    }

    private double obterAliquota(Produto produto) {
        Double aliquota = null;
        if (produto instanceof Eletronico) aliquota = ((Eletronico) produto).getAliquota();
        if (produto instanceof Vestuario) aliquota = ((Vestuario) produto).getAliquota();
        if (produto instanceof beans.Brinquedo) aliquota = ((beans.Brinquedo) produto).getAliquota();
        if (aliquota != null) return aliquota;
        return 0.0;
    }

}
