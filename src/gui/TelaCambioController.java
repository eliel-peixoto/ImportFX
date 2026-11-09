package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import negocio.CotacaoDoDia;
import negocio.IGerenciadorCambio;
import negocio.ServicoCotacaoDia;

/**
 * Tela de câmbio: mostra a cotação diária (PTAX) das moedas aceitas pelo sistema. */

public class TelaCambioController {

    private static final DateTimeFormatter HORA_CONSULTA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML private TableView<LinhaCotacao> tblCotacoes;
    @FXML private TableColumn<LinhaCotacao, String> colMoeda;
    @FXML private TableColumn<LinhaCotacao, String> colCompra;
    @FXML private TableColumn<LinhaCotacao, String> colVenda;
    @FXML private TableColumn<LinhaCotacao, String> colData;
    @FXML private TableColumn<LinhaCotacao, String> colSituacao;
    @FXML private Label lblAtualizadoEm;
    @FXML private Label lblAviso;
    @FXML private Button btnAtualizar;
    @FXML private Button btnVoltar;

    private IGerenciadorCambio gerenciadorCambio;
    private boolean consultando = false;

    @FXML
    private void initialize() {
        colMoeda.setCellValueFactory(new PropertyValueFactory<>("moeda"));
        colCompra.setCellValueFactory(new PropertyValueFactory<>("compra"));
        colVenda.setCellValueFactory(new PropertyValueFactory<>("venda"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
    }

    /** Chamado pelo menu principal; já dispara a primeira consulta ao abrir a tela. */
    public void setGerenciadorCambio(IGerenciadorCambio gerenciadorCambio) {
        this.gerenciadorCambio = gerenciadorCambio;
        atualizar();
    }

    @FXML
    private void handleAtualizar(ActionEvent event) {
        atualizar();
    }

    private void atualizar() {
        if (consultando || gerenciadorCambio == null) {
            return;
        }
        consultando = true;
        btnAtualizar.setDisable(true);
        mostrarAviso("");
        lblAtualizadoEm.setText("Consultando o Banco Central...");

        ServicoCotacaoDia servico = new ServicoCotacaoDia(gerenciadorCambio);
        Task<List<CotacaoDoDia>> tarefa = new Task<List<CotacaoDoDia>>() {
            @Override
            protected List<CotacaoDoDia> call() {
                return servico.consultarTodas(texto -> Platform.runLater(() -> lblAtualizadoEm.setText(texto)));
            }
        };
        tarefa.setOnSucceeded(e -> exibir(tarefa.getValue()));
        tarefa.setOnFailed(e -> {
            consultando = false;
            btnAtualizar.setDisable(false);
            lblAtualizadoEm.setText("Falha na consulta.");
            Throwable erro = tarefa.getException();
            mostrarAviso("Não foi possível consultar as cotações: "
                    + (erro == null || erro.getMessage() == null ? "erro inesperado." : erro.getMessage()));
        });

        Thread fio = new Thread(tarefa, "importfx-cambio");
        fio.setDaemon(true);
        fio.start();
    }

    private void exibir(List<CotacaoDoDia> cotacoes) {
        List<LinhaCotacao> linhas = new ArrayList<>();
        for (CotacaoDoDia c : cotacoes) {
            linhas.add(LinhaCotacao.de(c));
        }
        tblCotacoes.getItems().setAll(linhas);
        lblAtualizadoEm.setText("Consulta feita em " + LocalDateTime.now().format(HORA_CONSULTA));
        mostrarAviso(ServicoCotacaoDia.resumoAviso(cotacoes));
        consultando = false;
        btnAtualizar.setDisable(false);
    }

    private void mostrarAviso(String texto) {
        lblAviso.setText(texto);
        lblAviso.setVisible(!texto.isEmpty());
        lblAviso.setManaged(!texto.isEmpty());
    }

    @FXML
    private void handleVoltarMenu(ActionEvent event) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource("/gui/MenuPrincipal.fxml"));
            Stage janela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janela.setTitle("ImportFX - Menu Principal");
            janela.setScene(new Scene(raiz));
        } catch (Exception erro) {
            mostrarAviso("Não foi possível voltar ao menu: " + erro.getMessage());
        }
    }
}