package gui;

import beans.Brinquedo;
import beans.Cliente;
import beans.Cotacao;
import beans.Eletronico;
import beans.Fornecedor;
import beans.Item;
import beans.Moeda;
import beans.Pedido;
import beans.Produto;
import beans.Vestuario;
import dados.Repositorios;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import negocio.GerenciadorCliente;
import negocio.GerenciadorFornecedor;
import negocio.GerenciadorPedido;
import negocio.GerenciadorProduto;
import negocio.GerenciadorRelatorios;
import negocio.IGerenciadorCambio;
import negocio.IGerenciadorCliente;
import negocio.IGerenciadorFornecedor;
import negocio.IGerenciadorPedido;
import negocio.IGerenciadorProduto;
import negocio.IGerenciadorRelatorios;

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

    private final IGerenciadorCliente gerenciadorCliente =
            new GerenciadorCliente(Repositorios.clientes());
    private final IGerenciadorFornecedor gerenciadorFornecedor =
            new GerenciadorFornecedor(Repositorios.fornecedores());
    private final IGerenciadorProduto gerenciadorProduto =
            new GerenciadorProduto(Repositorios.produtos());

    private IGerenciadorPedido gerenciadorPedido =
            new GerenciadorPedido(Repositorios.pedidos());
    private IGerenciadorCambio gerenciadorCambio;
    private IGerenciadorRelatorios gerenciadorRelatorios =
            new GerenciadorRelatorios(Repositorios.pedidos());

    private final ArrayList<Item> itensTemporarios = new ArrayList<>();
    private boolean pedidoSalvo;
    private Cotacao cotacaoDoPedido;

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        if (gerenciadorPedido != null) {
            this.gerenciadorPedido = gerenciadorPedido;
            atualizarProximoIdNaTela();
        }
    }

    public void setGerenciadorCambio(IGerenciadorCambio gerenciadorCambio) {
        this.gerenciadorCambio = gerenciadorCambio;
    }

    public void setGerenciadorRelatorios(IGerenciadorRelatorios gerenciadorRelatorios) {
        if (gerenciadorRelatorios != null) this.gerenciadorRelatorios = gerenciadorRelatorios;
    }

    @FXML
    public void initialize() {
        txtId.setEditable(false);
        txtId.setFocusTraversable(false);
        atualizarProximoIdNaTela();

        configurarConversoresComboBox();
        carregarDadosReais();
        configurarFiltroProdutosPorFornecedor();
    }

    private void atualizarProximoIdNaTela() {
        if (txtId != null && gerenciadorPedido != null) {
            txtId.setText(String.valueOf(gerenciadorPedido.gerarProximoId()));
        }
    }

    private void configurarConversoresComboBox() {
        cbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                if (cliente == null) return "";
                return cliente.getNome();
            }

            @Override
            public Cliente fromString(String string) {
                return null;
            }
        });

        cbFornecedor.setConverter(new StringConverter<Fornecedor>() {
            @Override
            public String toString(Fornecedor fornecedor) {
                if (fornecedor == null) return "";
                return fornecedor.getNome() + " (" + fornecedor.getPaisOrigem()
                        + " - " + fornecedor.getMoedaPadrao() + ")";
            }

            @Override
            public Fornecedor fromString(String string) {
                return null;
            }
        });

        cbProduto.setConverter(new StringConverter<Produto>() {
            @Override
            public String toString(Produto produto) {
                if (produto == null) return "";
                double preco = produto.getPrecoMoedaOrigem() == null ? 0.0 : produto.getPrecoMoedaOrigem();
                return String.format(Locale.forLanguageTag("pt-BR"), "%s (%.2f)", produto.getNome(), preco);
            }

            @Override
            public Produto fromString(String string) {
                return null;
            }
        });
    }

    private void carregarDadosReais() {
        List<Cliente> clientes = gerenciadorCliente.listarTodos();
        List<Fornecedor> fornecedores = gerenciadorFornecedor.listarTodos();

        cbCliente.setItems(FXCollections.observableArrayList(clientes));
        cbFornecedor.setItems(FXCollections.observableArrayList(fornecedores));

        cbProduto.getItems().clear();
        cbProduto.setDisable(true);
        cbProduto.setPromptText("Selecione um fornecedor primeiro");

        if (clientes.isEmpty() || fornecedores.isEmpty()) {
            lblMensagem.setText("Aviso: Cadastre pelo menos um cliente e um fornecedor antes de criar um pedido.");
            lblMensagem.setTextFill(Color.ORANGE);
        }
    }

    private void configurarFiltroProdutosPorFornecedor() {
        cbFornecedor.valueProperty().addListener((obs, fornecedorAnterior, novoFornecedor) -> {
            if (fornecedorAnterior != null && !fornecedorAnterior.equals(novoFornecedor) && !itensTemporarios.isEmpty()) {
                itensTemporarios.clear();
                listViewItens.getItems().clear();
                cotacaoDoPedido = null;
                lblMensagem.setText("Fornecedor alterado: os itens anteriores foram removidos.");
                lblMensagem.setTextFill(Color.ORANGE);
            }

            cbProduto.getItems().clear();
            cbProduto.setValue(null);

            if (novoFornecedor == null) {
                cbProduto.setDisable(true);
                cbProduto.setPromptText("Selecione um fornecedor primeiro");
                return;
            }

            List<Produto> produtosDoFornecedor = gerenciadorProduto.listarTodos().stream()
                    .filter(p -> Objects.equals(p.getIdFornecedor(), novoFornecedor.getId()))
                    .collect(Collectors.toList());

            cbProduto.setItems(FXCollections.observableArrayList(produtosDoFornecedor));

            if (produtosDoFornecedor.isEmpty()) {
                cbProduto.setDisable(true);
                cbProduto.setPromptText("Nenhum produto cadastrado para este fornecedor");
            } else {
                cbProduto.setDisable(false);
                cbProduto.setPromptText("Selecione o Produto (" + novoFornecedor.getMoedaPadrao() + ")");
            }
        });
    }

    @FXML
    private void handleAdicionarItem(ActionEvent event) {
        try {
            Fornecedor fornecedorSelecionado = cbFornecedor.getValue();
            if (fornecedorSelecionado == null) {
                throw new Exception("Selecione o fornecedor do pedido.");
            }

            Produto produtoSelecionado = cbProduto.getValue();
            if (produtoSelecionado == null) {
                throw new Exception("Selecione um produto na lista.");
            }

            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            if (qtd <= 0) {
                throw new Exception("A quantidade deve ser maior que zero.");
            }

            double pesoCalculado = produtoSelecionado.getPeso() * qtd;
            double precoOriginal = produtoSelecionado.getPrecoMoedaOrigem() == null
                    ? 0.0 : produtoSelecionado.getPrecoMoedaOrigem();
            double precoUnitario = precoOriginal;
            Moeda moeda = fornecedorSelecionado.getMoedaPadrao();
            String simboloMoeda = moeda == Moeda.BRL ? "R$"
                    : moeda == null ? "N/I" : moeda.name();
            String mensagemConversao = "Item adicionado.";
            boolean convertido = moeda == Moeda.BRL;

            if (moeda == null) {
                throw new Exception("O fornecedor selecionado não possui moeda padrão definida.");
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
            lblMensagem.setTextFill(convertido ? Color.GREEN : Color.ORANGE);

        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro: Quantidade deve ser um número inteiro.");
            lblMensagem.setTextFill(Color.RED);
        } catch (Exception e) {
            lblMensagem.setText("Erro: " + e.getMessage());
            lblMensagem.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleSalvarPedido(ActionEvent event) {
        try {
            Long idAutomatico = gerenciadorPedido.gerarProximoId();

            Cliente clienteSelecionado = cbCliente.getValue();
            if (clienteSelecionado == null) {
                throw new Exception("Selecione um cliente para o pedido.");
            }

            Fornecedor fornecedorSelecionado = cbFornecedor.getValue();
            if (fornecedorSelecionado == null) {
                throw new Exception("Selecione um fornecedor para o pedido.");
            }

            if (itensTemporarios.isEmpty()) {
                throw new Exception("O pedido deve conter pelo menos um item.");
            }

            String status = txtStatus.getText();
            Pedido novoPedido = new Pedido(clienteSelecionado, fornecedorSelecionado, idAutomatico, status);

            for (Item item : itensTemporarios) {
                novoPedido.adicionar(item);
            }

            if (cotacaoDoPedido != null) {
                novoPedido.congelarCotacao(cotacaoDoPedido);
            }

            //Salva o pedido no sistema (atribui o ID sequencial automaticamente)
            gerenciadorPedido.criarPedido(novoPedido);
            pedidoSalvo = true;
            btnSalvarPedido.setDisable(true);
            txtId.setText(String.valueOf(novoPedido.getId()));

            //Gera o Extrato HTML (abrindo automaticamente no navegador) e a Fatura PDF
            String pastaDestino = System.getProperty("user.dir");
            gerenciadorPedido.exportarPedidoParaHtml(novoPedido.getId(), pastaDestino);
            File pdfGerado = gerenciadorPedido.exportarPedidoParaPdf(novoPedido.getId(), pastaDestino);

            lblMensagem.setText("Pedido #" + novoPedido.getId() + " salvo! HTML aberto e PDF gerado em: " + pdfGerado.getName());
            lblMensagem.setTextFill(Color.GREEN);

            //Redireciona para a tela de atualização de status
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gui/TelaAtualizarStatusPedido.fxml"));
            Parent root = loader.load();

            TelaAtualizarStatusController controller = loader.getController();
            controller.setGerenciadorPedido(this.gerenciadorPedido);
            controller.setGerenciadorRelatorios(this.gerenciadorRelatorios);

            Stage janelaPedido = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage janelaStatus = new Stage();
            janelaStatus.initOwner(janelaPedido);
            janelaStatus.setTitle("ImportFX - Atualizar Status");
            janelaStatus.setScene(new Scene(root));
            janelaStatus.show();

        } catch (Exception e) {
            lblMensagem.setText("Erro: " + e.getMessage());
            lblMensagem.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleAbrirRelatorios(ActionEvent event) {
        try {
            JanelaRelatorio.abrir(((Node) event.getSource()).getScene().getWindow(),
                    gerenciadorRelatorios);
        } catch (Exception erro) {
            lblMensagem.setText("Não foi possível abrir os relatórios: " + erro.getMessage());
            lblMensagem.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleVoltarMenu(ActionEvent event) {
        boolean temDadosNaoSalvos = !pedidoSalvo && !itensTemporarios.isEmpty();
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
            lblMensagem.setTextFill(Color.RED);
        }
    }

    private String nomeCategoria(Produto produto) {
        if (produto instanceof Eletronico) return "Eletrônico";
        if (produto instanceof Vestuario) return "Vestuário";
        if (produto instanceof Brinquedo) return "Brinquedo";
        return "Não informada";
    }

    private double obterAliquota(Produto produto) {
        Double aliquota = null;
        if (produto instanceof Eletronico) aliquota = ((Eletronico) produto).getAliquota();
        if (produto instanceof Vestuario) aliquota = ((Vestuario) produto).getAliquota();
        if (produto instanceof Brinquedo) aliquota = ((Brinquedo) produto).getAliquota();
        return aliquota != null ? aliquota : 0.0;
    }
}