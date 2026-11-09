package gui;

import beans.RelatorioPedido;
import beans.RelatorioProduto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import negocio.IGerenciadorRelatorios;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class TelaRelatorioController {
    @FXML private DatePicker dpPedidosInicio;
    @FXML private DatePicker dpPedidosFim;
    @FXML private DatePicker dpProdutosInicio;
    @FXML private DatePicker dpProdutosFim;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private Label lblResumoPedidos;
    @FXML private Label lblResumoProdutos;

    @FXML private TableView<RelatorioPedido> tblPedidos;
    @FXML private TableColumn<RelatorioPedido, Long> colPedidoId;
    @FXML private TableColumn<RelatorioPedido, String> colPedidoData;
    @FXML private TableColumn<RelatorioPedido, String> colPedidoCliente;
    @FXML private TableColumn<RelatorioPedido, String> colPedidoFornecedor;
    @FXML private TableColumn<RelatorioPedido, String> colPedidoStatus;
    @FXML private TableColumn<RelatorioPedido, String> colPedidoTotal;
    @FXML private TableColumn<RelatorioPedido, String> colPedidoImpostos;
    @FXML private TableColumn<RelatorioPedido, String> colPedidoConversao;

    @FXML private TableView<RelatorioProduto> tblProdutos;
    @FXML private TableColumn<RelatorioProduto, String> colProdutoCategoria;
    @FXML private TableColumn<RelatorioProduto, String> colProdutoNome;
    @FXML private TableColumn<RelatorioProduto, Integer> colProdutoQuantidade;
    @FXML private TableColumn<RelatorioProduto, String> colProdutoTotal;
    @FXML private TableColumn<RelatorioProduto, String> colProdutoConversao;

    private IGerenciadorRelatorios gerenciadorRelatorios;
    private List<RelatorioPedido> pedidosVisiveis = List.of();
    private List<RelatorioProduto> produtosVisiveis = List.of();

    @FXML
    public void initialize() {
        LocalDate hoje = LocalDate.now();
        dpPedidosInicio.setValue(hoje.withDayOfMonth(1));
        dpPedidosFim.setValue(hoje);
        dpProdutosInicio.setValue(hoje.withDayOfMonth(1));
        dpProdutosFim.setValue(hoje);
        cbCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Eletrônico", "Vestuário", "Brinquedo", "Não informada"));
        cbCategoria.getSelectionModel().selectFirst();

        colPedidoId.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colPedidoData.setCellValueFactory(new PropertyValueFactory<>("dataFormatada"));
        colPedidoCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colPedidoFornecedor.setCellValueFactory(new PropertyValueFactory<>("fornecedor"));
        colPedidoStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPedidoTotal.setCellValueFactory(new PropertyValueFactory<>("totalConvertidoFormatado"));
        colPedidoImpostos.setCellValueFactory(new PropertyValueFactory<>("impostosFormatados"));
        colPedidoConversao.setCellValueFactory(new PropertyValueFactory<>("situacaoConversao"));

        colProdutoCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colProdutoNome.setCellValueFactory(new PropertyValueFactory<>("produto"));
        colProdutoQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colProdutoTotal.setCellValueFactory(new PropertyValueFactory<>("totalConvertidoFormatado"));
        colProdutoConversao.setCellValueFactory(new PropertyValueFactory<>("situacaoConversao"));

        lblResumoPedidos.setText("Escolha o período e clique em Gerar relatório.");
        lblResumoProdutos.setText("Escolha o período e clique em Gerar relatório.");
    }

    public void setGerenciadorRelatorios(IGerenciadorRelatorios gerenciadorRelatorios) {
        this.gerenciadorRelatorios = gerenciadorRelatorios;
        gerarPedidos();
        gerarProdutos();
    }

    @FXML
    private void handleGerarPedidos() {
        gerarPedidos();
    }

    @FXML
    private void handleGerarProdutos() {
        gerarProdutos();
    }

    @FXML
    private void handleLimparPedidos() {
        tblPedidos.getItems().clear();
        pedidosVisiveis = List.of();
        lblResumoPedidos.setText("Relatório limpo. Escolha um período para gerar novamente.");
    }

    @FXML
    private void handleLimparProdutos() {
        tblProdutos.getItems().clear();
        produtosVisiveis = List.of();
        lblResumoProdutos.setText("Relatório limpo. Escolha um período para gerar novamente.");
    }

    @FXML
    private void handleVoltar() {
        ((Stage) tblPedidos.getScene().getWindow()).close();
    }

    @FXML
    private void handleExportarPedidos() {
        File destino = escolherArquivo("relatorio_pedidos.csv");
        if (destino == null) return;
        try {
            gerenciadorRelatorios.exportarPedidosCsv(pedidosVisiveis, destino.toPath());
            mostrarSucesso("Relatório de pedidos salvo em:\n" + destino.getAbsolutePath());
        } catch (IOException erro) {
            mostrarErro("Não foi possível salvar o CSV: " + erro.getMessage());
        }
    }

    @FXML
    private void handleExportarProdutos() {
        File destino = escolherArquivo("relatorio_produtos_categoria.csv");
        if (destino == null) return;
        try {
            gerenciadorRelatorios.exportarProdutosCsv(produtosVisiveis, destino.toPath());
            mostrarSucesso("Relatório de produtos salvo em:\n" + destino.getAbsolutePath());
        } catch (IOException erro) {
            mostrarErro("Não foi possível salvar o CSV: " + erro.getMessage());
        }
    }

    private void gerarPedidos() {
        if (gerenciadorRelatorios == null) return;
        try {
            pedidosVisiveis = gerenciadorRelatorios.relatorioPedidosPorPeriodo(
                    dpPedidosInicio.getValue(), dpPedidosFim.getValue());
            tblPedidos.setItems(FXCollections.observableArrayList(pedidosVisiveis));

            double totalConvertido = 0.0;
            double impostos = 0.0;
            int pendentes = 0;
            for (RelatorioPedido pedido : pedidosVisiveis) {
                totalConvertido += pedido.getTotalConvertido();
                impostos += pedido.getImpostosArrecadados();
                if (!pedido.isConversaoCompleta()) pendentes++;
            }
            lblResumoPedidos.setText(String.format(Locale.forLanguageTag("pt-BR"),
                    "%d pedido(s) | Totais convertidos: R$ %.2f | Impostos arrecadados: R$ %.2f | %d com conversão pendente",
                    pedidosVisiveis.size(), totalConvertido, impostos, pendentes));
        } catch (IllegalArgumentException erro) {
            mostrarAviso(erro.getMessage());
        }
    }

    private void gerarProdutos() {
        if (gerenciadorRelatorios == null) return;
        try {
            produtosVisiveis = gerenciadorRelatorios.relatorioProdutosMaisImportados(
                    dpProdutosInicio.getValue(), dpProdutosFim.getValue(), cbCategoria.getValue());
            tblProdutos.setItems(FXCollections.observableArrayList(produtosVisiveis));

            int unidades = 0;
            double totalConvertido = 0.0;
            for (RelatorioProduto produto : produtosVisiveis) {
                unidades += produto.getQuantidade();
                totalConvertido += produto.getTotalConvertido();
            }
            lblResumoProdutos.setText(String.format(Locale.forLanguageTag("pt-BR"),
                    "%d produto(s) | %d unidade(s) em pedidos pagos ou posteriores | Total convertido: R$ %.2f",
                    produtosVisiveis.size(), unidades, totalConvertido));
        } catch (IllegalArgumentException erro) {
            mostrarAviso(erro.getMessage());
        }
    }

    private File escolherArquivo(String nomeInicial) {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Salvar relatório CSV");
        seletor.setInitialFileName(nomeInicial);
        seletor.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivo CSV", "*.csv"));
        Window janela = tblPedidos.getScene().getWindow();
        return seletor.showSaveDialog(janela);
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem);
        alerta.setHeaderText("Erro ao exportar");
        alerta.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alerta.setHeaderText("CSV gerado");
        alerta.showAndWait();
    }
}
