package gui;

import beans.Fornecedor;
import beans.Moeda;
import dados.RepositorioFornecedores;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import negocio.GerenciadorFornecedor;

public class TelaFornecedorController {

    @FXML private TextField txtId;
    @FXML private TextField txtNome;
    @FXML private TextField txtPaisOrigem;
    @FXML private ComboBox<Moeda> cbMoedaPadrao;

    @FXML private Button btnSalvar;
    @FXML private Button btnRemover;
    @FXML private Button btnLimpar;

    @FXML private TableView<Fornecedor> tblFornecedores;
    @FXML private TableColumn<Fornecedor, Long> colId;
    @FXML private TableColumn<Fornecedor, String> colNome;
    @FXML private TableColumn<Fornecedor, String> colPaisOrigem;
    @FXML private TableColumn<Fornecedor, Moeda> colMoedaPadrao;

    private final GerenciadorFornecedor gerenciadorFornecedor;
    private ObservableList<Fornecedor> listaFornecedores;

    public TelaFornecedorController() {
        this.gerenciadorFornecedor = new GerenciadorFornecedor(new RepositorioFornecedores());
    }

    @FXML
    public void initialize() {
        // Popula o ComboBox com as moedas da enum
        cbMoedaPadrao.setItems(FXCollections.observableArrayList(Moeda.suportadas()));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPaisOrigem.setCellValueFactory(new PropertyValueFactory<>("paisOrigem"));
        colMoedaPadrao.setCellValueFactory(new PropertyValueFactory<>("moedaPadrao"));

        // Seleção na tabela carrega os dados nos campos
        tblFornecedores.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                txtId.setText(String.valueOf(selecionado.getId()));
                txtNome.setText(selecionado.getNome());
                txtPaisOrigem.setText(selecionado.getPaisOrigem());
                cbMoedaPadrao.setValue(selecionado.getMoedaPadrao());
            }
        });

        carregarTabela();
    }

    @FXML
    private void handleSalvar() {
        try {
            if (txtId.getText().trim().isEmpty() || txtNome.getText().trim().isEmpty() ||
                txtPaisOrigem.getText().trim().isEmpty() || cbMoedaPadrao.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha todos os campos e escolha a moeda.");
                return;
            }

            Long id = Long.valueOf(txtId.getText().trim());
            String nome = txtNome.getText().trim();
            String paisOrigem = txtPaisOrigem.getText().trim();
            Moeda moedaPadrao = cbMoedaPadrao.getValue();

            Fornecedor fornecedor = new Fornecedor(id, nome, paisOrigem, moedaPadrao);

            Fornecedor existente = gerenciadorFornecedor.buscarFornecedor(id);
            if (existente != null) {
                gerenciadorFornecedor.removerFornecedor(id);
            }

            gerenciadorFornecedor.cadastrarFornecedor(fornecedor);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor salvo com sucesso!");
            carregarTabela();
            handleLimpar();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "O campo ID deve conter apenas números válidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar fornecedor: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemover() {
        Fornecedor selecionado = tblFornecedores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum Selecionado", "Por favor, selecione um fornecedor na tabela para remover.");
            return;
        }

        try {
            gerenciadorFornecedor.removerFornecedor(selecionado.getId());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor removido com sucesso!");
            carregarTabela();
            handleLimpar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao remover fornecedor: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpar() {
        txtId.clear();
        txtNome.clear();
        txtPaisOrigem.clear();
        cbMoedaPadrao.setValue(null);
        tblFornecedores.getSelectionModel().clearSelection();
    }

    private void carregarTabela() {
        listaFornecedores = FXCollections.observableArrayList(gerenciadorFornecedor.listarTodos());
        tblFornecedores.setItems(listaFornecedores);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}