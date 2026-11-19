package gui;

import beans.Cliente;
import beans.Fornecedor;
import beans.Pedido;
import dados.Repositorios;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import negocio.GerenciadorCliente;
import negocio.GerenciadorFornecedor;
import negocio.GerenciadorPedido;
import negocio.GerenciadorRelatorios;
import negocio.IGerenciadorCliente;
import negocio.IGerenciadorFornecedor;
import negocio.IGerenciadorPedido;
import negocio.IGerenciadorRelatorios;

public class TelaAdminController {

    @FXML private Label lblTotalPedidos;
    @FXML private Label lblVolumeTotal;
    @FXML private Label lblTotalImpostos;
    @FXML private Label lblMensagemAdmin;

    @FXML private ComboBox<String> cbNovoStatusAdmin;
    @FXML private TableView<Pedido> tablePedidosAdmin;
    @FXML private TableColumn<Pedido, String> colPedId;
    @FXML private TableColumn<Pedido, String> colPedData;
    @FXML private TableColumn<Pedido, String> colPedCliente;
    @FXML private TableColumn<Pedido, String> colPedFornecedor;
    @FXML private TableColumn<Pedido, String> colPedImpostos;
    @FXML private TableColumn<Pedido, String> colPedTotal;
    @FXML private TableColumn<Pedido, String> colPedStatus;

    @FXML private TableView<Cliente> tableClientesAdmin;
    @FXML private TableColumn<Cliente, String> colCliId;
    @FXML private TableColumn<Cliente, String> colCliNome;
    @FXML private TableColumn<Cliente, String> colCliCpf;
    @FXML private TableColumn<Cliente, String> colCliEndereco;

    @FXML private TableView<Fornecedor> tableFornecedoresAdmin;
    @FXML private TableColumn<Fornecedor, String> colFornId;
    @FXML private TableColumn<Fornecedor, String> colFornNome;
    @FXML private TableColumn<Fornecedor, String> colFornPais;
    @FXML private TableColumn<Fornecedor, String> colFornMoeda;

    private IGerenciadorPedido gerenciadorPedido = GerenciadorPedido.getInstance();
    private final IGerenciadorCliente gerenciadorCliente =
            new GerenciadorCliente(Repositorios.clientes());
    private final IGerenciadorFornecedor gerenciadorFornecedor = GerenciadorFornecedor.getInstance();
    private IGerenciadorRelatorios gerenciadorRelatorios =
            new GerenciadorRelatorios(
                    Repositorios.produtos(),
                    Repositorios.clientes(),
                    Repositorios.fornecedores(),
                    Repositorios.pedidos()
            );

    /**
     * Método utilitário para a Tela de Login abrir diretamente o painel do Administrador.
     */
    public static void abrirParaAdmin(Stage stage,
                                      IGerenciadorPedido gp,
                                      IGerenciadorRelatorios gr) throws Exception {
        FXMLLoader loader = new FXMLLoader(TelaAdminController.class.getResource("/gui/TelaAdmin.fxml"));
        Parent root = loader.load();
        TelaAdminController controller = loader.getController();
        controller.setGerenciadorPedido(gp);
        controller.setGerenciadorRelatorios(gr);

        stage.setTitle("ImportFX - Painel do Administrador");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        if (gerenciadorPedido != null) {
            this.gerenciadorPedido = gerenciadorPedido;
            atualizarPainelCompleto();
        }
    }

    public void setGerenciadorRelatorios(IGerenciadorRelatorios gerenciadorRelatorios) {
        if (gerenciadorRelatorios != null) {
            this.gerenciadorRelatorios = gerenciadorRelatorios;
        }
    }

    @FXML
    public void initialize() {
        cbNovoStatusAdmin.getItems().addAll(
                "Pendente",
                "Aguardando Pagamento",
                "Pago",
                "Em Trânsito",
                "Alfândega",
                "Entregue"
        );

        configurarTabelas();
        atualizarPainelCompleto();
    }

    private void configurarTabelas() {
        Locale ptBR = Locale.forLanguageTag("pt-BR");
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Tabela de Pedidos
        colPedId.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getId()));
        colPedData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataCriacao() != null ? c.getValue().getDataCriacao().format(fmtData) : "-"));
        colPedCliente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCliente() != null
                        ? "ID " + c.getValue().getCliente().getId() + " - " + c.getValue().getCliente().getNome()
                        : "-"));
        colPedFornecedor.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFornecedor() != null
                        ? "ID " + c.getValue().getFornecedor().getId() + " - " + c.getValue().getFornecedor().getNome()
                        : "-"));
        colPedImpostos.setCellValueFactory(c -> new SimpleStringProperty(
                String.format(ptBR, "R$ %.2f", c.getValue().getTotalImpostos())));
        colPedTotal.setCellValueFactory(c -> new SimpleStringProperty(
                String.format(ptBR, "R$ %.2f", c.getValue().getValorTotalFinal())));
        colPedStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        // Tabela de Clientes
        colCliId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colCliNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCliCpf.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCpf() != null ? String.format("%011d", c.getValue().getCpf()) : "-"));
        colCliEndereco.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEndereco() != null ? c.getValue().getEndereco() : "-"));

        // Tabela de Fornecedores
        colFornId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colFornNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colFornPais.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPaisOrigem() != null ? c.getValue().getPaisOrigem() : "-"));
        colFornMoeda.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMoedaPadrao() != null ? c.getValue().getMoedaPadrao().name() : "-"));
    }

    private void atualizarPainelCompleto() {
        Locale ptBR = Locale.forLanguageTag("pt-BR");

        // 1. Atualiza Pedidos e Totais
        List<Pedido> todosPedidos = gerenciadorPedido.listarPedidos();
        tablePedidosAdmin.setItems(FXCollections.observableArrayList(todosPedidos));
        tablePedidosAdmin.refresh();

        double volumeTotal = 0.0;
        double impostosTotal = 0.0;
        for (Pedido p : todosPedidos) {
            volumeTotal += p.getValorTotalFinal();
            impostosTotal += p.getTotalImpostos();
        }

        lblTotalPedidos.setText(String.valueOf(todosPedidos.size()));
        lblVolumeTotal.setText(String.format(ptBR, "R$ %.2f", volumeTotal));
        lblTotalImpostos.setText(String.format(ptBR, "R$ %.2f", impostosTotal));

        // 2. Atualiza Clientes e Fornecedores Cadastrados
        tableClientesAdmin.setItems(FXCollections.observableArrayList(gerenciadorCliente.listarTodos()));
        tableFornecedoresAdmin.setItems(FXCollections.observableArrayList(gerenciadorFornecedor.listarTodos()));
    }

    @FXML
    private void handleAtualizarStatusPedido(ActionEvent event) {
        Pedido selecionado = tablePedidosAdmin.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            lblMensagemAdmin.setText("Aviso: Selecione um pedido na tabela antes de alterar o status.");
            lblMensagemAdmin.setTextFill(Color.ORANGE);
            return;
        }

        String novoStatus = cbNovoStatusAdmin.getValue();
        if (novoStatus == null) {
            lblMensagemAdmin.setText("Aviso: Selecione o novo status na lista.");
            lblMensagemAdmin.setTextFill(Color.ORANGE);
            return;
        }

        try {
            Pedido atualizacao = new Pedido(
                    selecionado.getCliente(),
                    selecionado.getFornecedor(),
                    selecionado.getId(),
                    novoStatus
            );
            gerenciadorPedido.atualizarStatusPedido(atualizacao);

            cbNovoStatusAdmin.getSelectionModel().clearSelection();
            atualizarPainelCompleto();

            lblMensagemAdmin.setText("Pedido #" + selecionado.getId() + " atualizado para \""
                    + novoStatus + "\" (Notificação enviada ao cliente).");
            lblMensagemAdmin.setTextFill(Color.GREEN);
        } catch (Exception e) {
            lblMensagemAdmin.setText("Erro ao atualizar status: " + e.getMessage());
            lblMensagemAdmin.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleGerenciarCadastros(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaCadastros.fxml"));
            Parent root = loader.load();

            TelaCadastrosController controller = loader.getController();
            controller.configurarModoAdmin(true, gerenciadorPedido, gerenciadorRelatorios);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("ImportFX - Gestão de Cadastros (Administrador)");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            lblMensagemAdmin.setText("Erro ao abrir gestão de cadastros: " + e.getMessage());
            lblMensagemAdmin.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleAbrirRelatorios(ActionEvent event) {
        try {
            JanelaRelatorio.abrir(((Node) event.getSource()).getScene().getWindow(),
                    gerenciadorRelatorios);
        } catch (Exception erro) {
            lblMensagemAdmin.setText("Não foi possível abrir a janela de relatórios: " + erro.getMessage());
            lblMensagemAdmin.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleAtualizarTudo(ActionEvent event) {
        atualizarPainelCompleto();
        lblMensagemAdmin.setText("Listas e totais atualizados.");
        lblMensagemAdmin.setTextFill(Color.GREEN);
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
}