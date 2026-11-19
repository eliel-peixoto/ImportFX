package gui;

import beans.Cliente;
import beans.Notificacao;
import beans.Pedido;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import negocio.CotacaoDoDia;
import negocio.GerenciadorCliente;
import negocio.GerenciadorPedido;
import negocio.GerenciadorRelatorios;
import negocio.IGerenciadorCambio;
import negocio.IGerenciadorCliente;
import negocio.IGerenciadorPedido;
import negocio.IGerenciadorRelatorios;
import negocio.ServicoCotacaoDia;

public class TelaClienteController {

    @FXML private Label lblNomeCliente;
    @FXML private Label lblIdCliente;
    @FXML private Label lblBadgeNotificacoes;
    @FXML private Button btnMarcarLidas;
    @FXML private ListView<Notificacao> listViewNotificacoes;

    @FXML private TableView<Pedido> tablePedidosCliente;
    @FXML private TableColumn<Pedido, String> colIdPedido;
    @FXML private TableColumn<Pedido, String> colFornecedor;
    @FXML private TableColumn<Pedido, String> colData;
    @FXML private TableColumn<Pedido, String> colTotal;
    @FXML private TableColumn<Pedido, String> colStatus;

    @FXML private Label lblMensagemCliente;

    private Cliente clienteLogado;
    private final IGerenciadorCliente gerenciadorCliente =
            new GerenciadorCliente(Repositorios.clientes());
    private IGerenciadorPedido gerenciadorPedido = GerenciadorPedido.getInstance();
    private IGerenciadorCambio gerenciadorCambio;
    private IGerenciadorRelatorios gerenciadorRelatorios =
            new GerenciadorRelatorios(Repositorios.pedidos());

    /**
     * Método utilitário para o colaborador da Tela de Login abrir o painel do cliente em 1 linha.
     */
    public static void abrirParaCliente(Stage stage, Cliente cliente,
                                        IGerenciadorPedido gp,
                                        IGerenciadorCambio gc,
                                        IGerenciadorRelatorios gr) throws Exception {
        FXMLLoader loader = new FXMLLoader(TelaClienteController.class.getResource("/gui/TelaCliente.fxml"));
        Parent root = loader.load();
        TelaClienteController controller = loader.getController();
        controller.setGerenciadorPedido(gp);
        controller.setGerenciadorCambio(gc);
        controller.setGerenciadorRelatorios(gr);

        stage.setTitle("ImportFX - Área do Cliente");
        stage.setScene(new Scene(root));
        stage.show();

        controller.setClienteLogado(cliente);
    }

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
        atualizarPainelCompleto();
        exibirPopupNovasNotificacoes();
    }

    public void setGerenciadorPedido(IGerenciadorPedido gerenciadorPedido) {
        if (gerenciadorPedido != null) {
            this.gerenciadorPedido = gerenciadorPedido;
            atualizarTabelaPedidos();
        }
    }

    public void setGerenciadorCambio(IGerenciadorCambio gerenciadorCambio) {
        this.gerenciadorCambio = gerenciadorCambio;
    }

    public void setGerenciadorRelatorios(IGerenciadorRelatorios gerenciadorRelatorios) {
        if (gerenciadorRelatorios != null) {
            this.gerenciadorRelatorios = gerenciadorRelatorios;
        }
    }

    @FXML
    public void initialize() {
        configurarTabelaPedidos();
        configurarVisualNotificacoes();
    }

    private void configurarTabelaPedidos() {
        Locale ptBR = Locale.forLanguageTag("pt-BR");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        colIdPedido.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getId()));
        colFornecedor.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFornecedor() != null ? c.getValue().getFornecedor().getNome() : "-"));
        colData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataCriacao() != null ? c.getValue().getDataCriacao().format(fmt) : "-"));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                String.format(ptBR, "R$ %.2f", c.getValue().getValorTotalFinal())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }

    private void configurarVisualNotificacoes() {
        // Destaca visualmente as notificações não lidas em laranja claro e negrito
        listViewNotificacoes.setCellFactory(lv -> new ListCell<Notificacao>() {
            @Override
            protected void updateItem(Notificacao item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (!item.isLida()) {
                        setStyle("-fx-background-color: #fff0e8; -fx-border-color: #ff4501; "
                                + "-fx-border-width: 0 0 0 4; -fx-font-weight: bold; -fx-text-fill: #b32d00;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: #555555;");
                    }
                }
            }
        });
    }

    private void atualizarPainelCompleto() {
        if (clienteLogado == null) return;

        // Sincroniza com o cadastro mais recente do repositório
        Cliente atualizado = gerenciadorCliente.buscarCliente(clienteLogado.getId());
        if (atualizado != null) {
            this.clienteLogado = atualizado;
        }

        lblNomeCliente.setText("Bem-vindo: " + clienteLogado.getNome());
        lblIdCliente.setText("ID: " + clienteLogado.getId());

        atualizarNotificacoesNaTela();
        atualizarTabelaPedidos();
    }

    private void atualizarNotificacoesNaTela() {
        if (clienteLogado == null) return;

        List<Notificacao> lista = clienteLogado.getNotificacoes();
        listViewNotificacoes.setItems(FXCollections.observableArrayList(lista));

        long naoLidas = clienteLogado.getQuantidadeNotificacoesNaoLidas();
        if (naoLidas > 0) {
            lblBadgeNotificacoes.setText(naoLidas + (naoLidas == 1 ? " NOVA MENSAGEM" : " NOVAS MENSAGENS"));
            lblBadgeNotificacoes.setStyle("-fx-background-color: #ff4501; -fx-text-fill: white; "
                    + "-fx-padding: 4 12 4 12; -fx-background-radius: 12; -fx-font-weight: bold;");
            btnMarcarLidas.setDisable(false);
        } else {
            lblBadgeNotificacoes.setText("Nenhuma nova notificação");
            lblBadgeNotificacoes.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; "
                    + "-fx-padding: 3 10 3 10; -fx-background-radius: 12; -fx-font-weight: bold;");
            btnMarcarLidas.setDisable(true);
        }
    }

    private void atualizarTabelaPedidos() {
        if (clienteLogado == null || gerenciadorPedido == null) return;

        List<Pedido> meusPedidos = gerenciadorPedido.listarPedidos().stream()
                .filter(p -> p.getCliente() != null && Objects.equals(p.getCliente().getId(), clienteLogado.getId()))
                .collect(Collectors.toList());

        tablePedidosCliente.setItems(FXCollections.observableArrayList(meusPedidos));
    }

    private void exibirPopupNovasNotificacoes() {
        if (this.clienteLogado == null) return;

        List<Notificacao> pendentesPopup = this.clienteLogado.getNotificacoes().stream()
                .filter(n -> !n.isPopupExibido())
                .collect(Collectors.toList());

        if (pendentesPopup.isEmpty()) return;

        StringBuilder texto = new StringBuilder();
        for (Notificacao n : pendentesPopup) {
            n.setPopupExibido(true);
            texto.append("• ").append(n.getMensagem()).append("\n");
        }

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        if (lblNomeCliente != null && lblNomeCliente.getScene() != null) {
            alerta.initOwner(lblNomeCliente.getScene().getWindow());
        }
        alerta.setTitle("ImportFX - Atualização de Pedido");
        alerta.setHeaderText("Você tem " + pendentesPopup.size() + " nova(s) atualização(ões) de status!");
        alerta.setContentText(texto.toString().trim());
        alerta.showAndWait();
    }

    @FXML
    private void handleVerUltimasAlteracoes(ActionEvent event) {
        if (clienteLogado == null) return;

        atualizarPainelCompleto();
        List<Notificacao> alteracoes = clienteLogado.getNotificacoes();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(((Node) event.getSource()).getScene().getWindow());
        modal.setTitle("ImportFX - Últimas Alterações de Pedidos");

        TableView<Notificacao> tabela = new TableView<>();
        tabela.setPlaceholder(new Label("Nenhuma alteração de status registrada para seus pedidos."));

        TableColumn<Notificacao, String> colDataHora = new TableColumn<>("Data / Hora");
        colDataHora.setPrefWidth(145);
        colDataHora.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataHora() != null ? c.getValue().getDataHora().format(fmt) : "-"));

        TableColumn<Notificacao, String> colPedido = new TableColumn<>("Pedido");
        colPedido.setPrefWidth(85);
        colPedido.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getIdPedido() != null ? "#" + c.getValue().getIdPedido() : "-"));

        TableColumn<Notificacao, String> colDescricao = new TableColumn<>("Alteração de Status");
        colDescricao.setPrefWidth(420);
        colDescricao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMensagem()));

        TableColumn<Notificacao, String> colSituacao = new TableColumn<>("Situação");
        colSituacao.setPrefWidth(95);
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isLida() ? "Lida" : "Nova"));

        tabela.getColumns().addAll(List.of(colDataHora, colPedido, colDescricao, colSituacao));
        tabela.setItems(FXCollections.observableArrayList(alteracoes));

        Label lblResumo = new Label("Total de alterações registradas: " + alteracoes.size());

        Button btnMarcar = new Button("Marcar todas como lidas");
        btnMarcar.setOnAction(e -> {
            clienteLogado.marcarTodasNotificacoesComoLidas();
            tabela.refresh();
            atualizarNotificacoesNaTela();
        });

        Button btnFechar = new Button("Fechar");
        btnFechar.setOnAction(e -> modal.close());

        HBox rodape = new HBox(12, lblResumo, btnMarcar, btnFechar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(12,
                new Label("Histórico de alterações de status dos pedidos de " + clienteLogado.getNome() + ":"),
                tabela,
                rodape);
        layout.setPadding(new Insets(16));

        modal.setScene(new Scene(layout, 780, 420));
        modal.showAndWait();
    }

    @FXML
    private void handleMarcarNotificacoesLidas(ActionEvent event) {
        if (clienteLogado == null) return;
        clienteLogado.marcarTodasNotificacoesComoLidas();
        atualizarNotificacoesNaTela();
    }

    @FXML
    private void handleAtualizarDados(ActionEvent event) {
        atualizarPainelCompleto();
        lblMensagemCliente.setText("Painel atualizado.");
        lblMensagemCliente.setTextFill(Color.GREEN);
    }

    @FXML
    private void handleCriarPedido(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaCriarPedido.fxml"));
            Parent root = loader.load();

            TelaCriarPedidoController controller = loader.getController();
            controller.setGerenciadorPedido(this.gerenciadorPedido);
            controller.setGerenciadorCambio(this.gerenciadorCambio);
            controller.setGerenciadorRelatorios(this.gerenciadorRelatorios);
            controller.configurarParaCliente(this.clienteLogado);

            Stage stage = new Stage();
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("ImportFX - Novo Pedido (" + clienteLogado.getNome() + ")");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            atualizarPainelCompleto();

            if (controller.getPedidoCriado() != null) {
                String nomePdf = controller.getUltimoPdfGerado() != null
                        ? " | Fatura PDF: " + controller.getUltimoPdfGerado().getName() : "";
                lblMensagemCliente.setText("Pedido #" + controller.getPedidoCriado().getId()
                        + " criado! Extrato HTML aberto no navegador" + nomePdf);
                lblMensagemCliente.setTextFill(Color.GREEN);
            }
        } catch (Exception e) {
            lblMensagemCliente.setText("Erro ao abrir criação de pedido: " + e.getMessage());
            lblMensagemCliente.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleVerExtratoHtml(ActionEvent event) {
        Pedido selecionado = tablePedidosCliente.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            lblMensagemCliente.setText("Selecione um pedido na tabela acima para abrir o extrato.");
            lblMensagemCliente.setTextFill(Color.ORANGE);
            return;
        }
        try {
            gerenciadorPedido.exportarPedidoParaHtml(selecionado.getId(), System.getProperty("user.dir"));
            lblMensagemCliente.setText("Extrato HTML do Pedido #" + selecionado.getId() + " aberto no navegador.");
            lblMensagemCliente.setTextFill(Color.GREEN);
        } catch (Exception e) {
            lblMensagemCliente.setText("Erro ao gerar extrato: " + e.getMessage());
            lblMensagemCliente.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleVerCotacaoDiaria(ActionEvent event) {
        if (gerenciadorCambio == null) {
            lblMensagemCliente.setText("Serviço de câmbio não inicializado.");
            lblMensagemCliente.setTextFill(Color.ORANGE);
            return;
        }

        ServicoCotacaoDia servico = new ServicoCotacaoDia(gerenciadorCambio);
        List<CotacaoDoDia> cotacoes = servico.consultarTodas(msg -> {});

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(((Node) event.getSource()).getScene().getWindow());
        modal.setTitle("ImportFX - Cotação Diária (PTAX)");

        TableView<CotacaoDoDia> tabela = new TableView<>();
        TableColumn<CotacaoDoDia, String> colMoeda = new TableColumn<>("Moeda");
        colMoeda.setPrefWidth(100);
        colMoeda.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMoeda().name()));

        TableColumn<CotacaoDoDia, String> colValor = new TableColumn<>("Valor em Reais (R$)");
        colValor.setPrefWidth(160);
        colValor.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isDisponivel()
                        ? String.format(Locale.forLanguageTag("pt-BR"), "R$ %.4f", c.getValue().getCotacao().getVenda())
                        : "Indisponível"));

        TableColumn<CotacaoDoDia, String> colObs = new TableColumn<>("Origem");
        colObs.setPrefWidth(160);
        colObs.setCellValueFactory(c -> new SimpleStringProperty(
                !c.getValue().isDisponivel() ? "Sem conexão"
                        : c.getValue().isDesatualizada() ? "Cache local" : "PTAX Banco Central"));

        tabela.getColumns().addAll(List.of(colMoeda, colValor, colObs));
        tabela.setItems(FXCollections.observableArrayList(cotacoes));

        Label aviso = new Label(ServicoCotacaoDia.resumoAviso(cotacoes));
        aviso.setWrapText(true);

        VBox box = new VBox(12, new Label("Cotações PTAX Disponíveis:"), tabela, aviso);
        box.setPadding(new Insets(16));
        modal.setScene(new Scene(box, 460, 380));
        modal.show();
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