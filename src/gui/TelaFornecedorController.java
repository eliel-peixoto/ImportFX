package gui;

import beans.Brinquedo;
import beans.Eletronico;
import beans.Fornecedor;
import beans.Item;
import beans.Pedido;
import beans.Produto;
import beans.Vestuario;
import dados.Repositorios;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import negocio.GerenciadorFornecedor;
import negocio.GerenciadorPedido;
import negocio.GerenciadorProduto;
import negocio.IGerenciadorFornecedor;
import negocio.IGerenciadorPedido;
import negocio.IGerenciadorProduto;

public class TelaFornecedorController {

    @FXML private Label lblNomeFornecedor;
    @FXML private Label lblDetalhesFornecedor;
    @FXML private Label lblIdFornecedor;
    @FXML private Label lblMensagemFornecedor;

    @FXML private TextField txtAtualizarEstoque;

    @FXML private TableView<Produto> tableProdutosFornecedor;
    @FXML private TableColumn<Produto, String> colProdId;
    @FXML private TableColumn<Produto, String> colProdNome;
    @FXML private TableColumn<Produto, String> colProdCategoria;
    @FXML private TableColumn<Produto, String> colProdPreco;
    @FXML private TableColumn<Produto, String> colProdPeso;
    @FXML private TableColumn<Produto, String> colProdAliquota;
    @FXML private TableColumn<Produto, String> colProdEstoque;

    @FXML private TableView<Pedido> tablePedidosFornecedor;
    @FXML private TableColumn<Pedido, String> colPedId;
    @FXML private TableColumn<Pedido, String> colPedCliente;
    @FXML private TableColumn<Pedido, String> colPedItens;
    @FXML private TableColumn<Pedido, String> colPedData;
    @FXML private TableColumn<Pedido, String> colPedStatus;

    private Fornecedor fornecedorLogado;
    private final IGerenciadorFornecedor gerenciadorFornecedor =
            new GerenciadorFornecedor(Repositorios.fornecedores());
    private IGerenciadorProduto gerenciadorProduto =
            new GerenciadorProduto(Repositorios.produtos());
    private IGerenciadorPedido gerenciadorPedido =
            new GerenciadorPedido(Repositorios.pedidos());

    public static void abrirParaFornecedor(Stage stage, Fornecedor fornecedor,
                                           IGerenciadorPedido gp,
                                           IGerenciadorProduto gProd) throws Exception {
        FXMLLoader loader = new FXMLLoader(TelaFornecedorController.class.getResource("/gui/TelaFornecedor.fxml"));
        Parent root = loader.load();
        TelaFornecedorController controller = loader.getController();
        controller.setGerenciadorPedido(gp);
        controller.setGerenciadorProduto(gProd);
        controller.setFornecedorLogado(fornecedor);

        stage.setTitle("ImportFX - Área do Fornecedor");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setFornecedorLogado(Fornecedor fornecedor) {
        this.fornecedorLogado = fornecedor;
        atualizarPainelCompleto();
    }

    public void setGerenciadorProduto(IGerenciadorProduto gerenciadorProduto) {
        if (gerenciadorProduto != null) {
            this.gerenciadorProduto = gerenciadorProduto;
            atualizarTabelaProdutos();
        }
    }

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        if (gerenciadorPedido != null) {
            this.gerenciadorPedido = gerenciadorPedido;
            atualizarTabelaPedidos();
        }
    }

    @FXML
    public void initialize() {
        configurarTabelas();

        if (this.fornecedorLogado == null) {
            List<Fornecedor> cadastrados = gerenciadorFornecedor.listarTodos();
            if (!cadastrados.isEmpty()) {
                setFornecedorLogado(cadastrados.get(0));
            }
        }
    }

    private void configurarTabelas() {
        Locale ptBR = Locale.forLanguageTag("pt-BR");
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        //Tabela de Produtos / Estoque
        colProdId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colProdNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colProdCategoria.setCellValueFactory(c -> new SimpleStringProperty(obterNomeCategoria(c.getValue())));
        colProdPreco.setCellValueFactory(c -> {
            String moeda = (fornecedorLogado != null && fornecedorLogado.getMoedaPadrao() != null)
                    ? fornecedorLogado.getMoedaPadrao().name() : "";
            double preco = c.getValue().getPrecoMoedaOrigem() != null ? c.getValue().getPrecoMoedaOrigem() : 0.0;
            return new SimpleStringProperty(String.format(ptBR, "%s %.2f", moeda, preco).trim());
        });
        colProdPeso.setCellValueFactory(c -> new SimpleStringProperty(
                String.format(ptBR, "%.2f kg", c.getValue().getPeso() != null ? c.getValue().getPeso() : 0.0)));
        colProdAliquota.setCellValueFactory(c -> new SimpleStringProperty(
                String.format(ptBR, "%.1f%%", obterAliquota(c.getValue()))));
        colProdEstoque.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getQuantidadeEstoque() + " un."));

        //Tabela de Pedidos do Fornecedor
        colPedId.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getId()));
        colPedCliente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCliente() != null ? c.getValue().getCliente().getNome() : "-"));
        colPedItens.setCellValueFactory(c -> new SimpleStringProperty(resumirItensPedido(c.getValue())));
        colPedData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataCriacao() != null ? c.getValue().getDataCriacao().format(fmtData) : "-"));
        colPedStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }

    private void atualizarPainelCompleto() {
        if (fornecedorLogado == null) return;

        Fornecedor atualizado = gerenciadorFornecedor.buscarFornecedor(fornecedorLogado.getId());
        if (atualizado != null) {
            this.fornecedorLogado = atualizado;
        }

        String moeda = fornecedorLogado.getMoedaPadrao() != null ? fornecedorLogado.getMoedaPadrao().name() : "N/I";
        lblNomeFornecedor.setText("Fornecedor: " + fornecedorLogado.getNome());
        lblDetalhesFornecedor.setText("País de Origem: " + fornecedorLogado.getPaisOrigem() + "  |  Moeda Padrão: " + moeda);
        lblIdFornecedor.setText("ID: " + fornecedorLogado.getId());

        atualizarTabelaProdutos();
        atualizarTabelaPedidos();
    }

    private void atualizarTabelaProdutos() {
        if (fornecedorLogado == null || gerenciadorProduto == null) return;

        List<Produto> meusProdutos = gerenciadorProduto.listarTodos().stream()
                .filter(p -> Objects.equals(p.getIdFornecedor(), fornecedorLogado.getId()))
                .collect(Collectors.toList());

        tableProdutosFornecedor.setItems(FXCollections.observableArrayList(meusProdutos));
        tableProdutosFornecedor.refresh();
    }

    private void atualizarTabelaPedidos() {
        if (fornecedorLogado == null || gerenciadorPedido == null) return;

        List<Pedido> meusPedidos = gerenciadorPedido.listarPedidos().stream()
                .filter(p -> p.getFornecedor() != null
                        && Objects.equals(p.getFornecedor().getId(), fornecedorLogado.getId()))
                .collect(Collectors.toList());

        tablePedidosFornecedor.setItems(FXCollections.observableArrayList(meusPedidos));
        tablePedidosFornecedor.refresh();
    }

    @FXML
    private void handleAbrirCadastroProduto(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaCadProduto.fxml"));
            Parent root = loader.load();

            TelaProdutoController controller = loader.getController();
            controller.configurarParaFornecedor(this.fornecedorLogado);

            Stage modal = new Stage();
            modal.initOwner(((Node) event.getSource()).getScene().getWindow());
            modal.initModality(Modality.WINDOW_MODAL);
            modal.setTitle("ImportFX - Cadastro de Produtos");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            //Ao fechar a tela de cadastro, recarrega a lista de produtos/estoque automaticamente
            atualizarTabelaProdutos();
            lblMensagemFornecedor.setText("Catálogo de produtos atualizado.");
            lblMensagemFornecedor.setTextFill(Color.GREEN);
        } catch (Exception e) {
            lblMensagemFornecedor.setText("Erro ao abrir cadastro de produto: " + e.getMessage());
            lblMensagemFornecedor.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleAtualizarEstoque(ActionEvent event) {
        Produto selecionado = tableProdutosFornecedor.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            lblMensagemFornecedor.setText("Aviso: Selecione um produto na tabela para atualizar o estoque.");
            lblMensagemFornecedor.setTextFill(Color.ORANGE);
            return;
        }
        try {
            int novaQtd = Integer.parseInt(txtAtualizarEstoque.getText().trim());
            if (novaQtd < 0) {
                throw new Exception("A quantidade em estoque não pode ser negativa.");
            }
            selecionado.setQuantidadeEstoque(novaQtd);
            gerenciadorProduto.atualizarProduto(selecionado);
            txtAtualizarEstoque.clear();
            atualizarTabelaProdutos();

            lblMensagemFornecedor.setText("Estoque de \"" + selecionado.getNome() + "\" atualizado para " + novaQtd + " un.");
            lblMensagemFornecedor.setTextFill(Color.GREEN);
        } catch (NumberFormatException e) {
            lblMensagemFornecedor.setText("Erro: Digite um número inteiro válido para o estoque.");
            lblMensagemFornecedor.setTextFill(Color.RED);
        } catch (Exception e) {
            lblMensagemFornecedor.setText("Erro: " + e.getMessage());
            lblMensagemFornecedor.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleRemoverProduto(ActionEvent event) {
        Produto selecionado = tableProdutosFornecedor.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            lblMensagemFornecedor.setText("Aviso: Selecione um produto na tabela para remover.");
            lblMensagemFornecedor.setTextFill(Color.ORANGE);
            return;
        }
        try {
            gerenciadorProduto.removerProduto(selecionado.getId());
            if (fornecedorLogado != null && fornecedorLogado.getListaProdutos() != null) {
                fornecedorLogado.getListaProdutos().remover(selecionado.getId());
            }
            atualizarTabelaProdutos();

            lblMensagemFornecedor.setText("Produto \"" + selecionado.getNome() + "\" removido.");
            lblMensagemFornecedor.setTextFill(Color.GREEN);
        } catch (Exception e) {
            lblMensagemFornecedor.setText("Erro ao remover: " + e.getMessage());
            lblMensagemFornecedor.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleAtualizarPainel(ActionEvent event) {
        atualizarPainelCompleto();
        lblMensagemFornecedor.setText("Dados atualizados.");
        lblMensagemFornecedor.setTextFill(Color.GREEN);
    }

    @FXML
    private void handleSair(ActionEvent event) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource("/gui/TelaLogin.fxml"));
            Stage janela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janela.setTitle("ImportFX - Acesso ao Sistema");
            janela.setScene(new Scene(raiz));
        } catch (Exception e) {
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
    }

    private String obterNomeCategoria(Produto p) {
        if (p instanceof Eletronico) return "Eletrônico";
        if (p instanceof Vestuario) return "Vestuário";
        if (p instanceof Brinquedo) return "Brinquedo";
        return "Geral";
    }

    private double obterAliquota(Produto p) {
        if (p instanceof Eletronico e && e.getAliquota() != null) return e.getAliquota();
        if (p instanceof Vestuario v && v.getAliquota() != null) return v.getAliquota();
        if (p instanceof Brinquedo b && b.getAliquota() != null) return b.getAliquota();
        return 0.0;
    }

    private String resumirItensPedido(Pedido pedido) {
        if (pedido.getItemPedido() == null || pedido.getItemPedido().isEmpty()) return "Sem itens";
        StringBuilder sb = new StringBuilder();
        for (Item item : pedido.getItemPedido()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(item.getQuantidade()).append("x ").append(item.getNomeProduto());
        }
        return sb.toString();
    }
}