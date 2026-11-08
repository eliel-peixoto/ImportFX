package gui;

import beans.Cliente;
import dados.RepositorioCliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import negocio.GerenciadorCliente;

public class TelaClienteController {

    @FXML private TextField txtId;
    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtEndereco;

    @FXML private Button btnSalvar;
    @FXML private Button btnRemover;
    @FXML private Button btnLimpar;

    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Long> colId;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, Long> colCpf;
    @FXML private TableColumn<Cliente, String> colEndereco;

    private final GerenciadorCliente gerenciadorCliente;
    private ObservableList<Cliente> listaClientes;

    public TelaClienteController() {
        this.gerenciadorCliente = new GerenciadorCliente(new RepositorioCliente());
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        // Seleção na tabela carrega os dados nos campos
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                txtId.setText(String.valueOf(selecionado.getId()));
                txtNome.setText(selecionado.getNome());
                txtCpf.setText(String.valueOf(selecionado.getCpf()));
                txtEndereco.setText(selecionado.getEndereco());
            }
        });

        carregarTabela();
    }

    @FXML
    private void handleSalvar() {
        try {
            if (txtId.getText().trim().isEmpty() || txtNome.getText().trim().isEmpty() ||
                txtCpf.getText().trim().isEmpty() || txtEndereco.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha todos os campos do formulário.");
                return;
            }

            Long id = Long.valueOf(txtId.getText().trim());
            String nome = txtNome.getText().trim();
            Long cpf = Long.valueOf(txtCpf.getText().trim());
            String endereco = txtEndereco.getText().trim();

            Cliente cliente = new Cliente(id, nome, cpf, endereco);
            
            Cliente existente = gerenciadorCliente.buscarCliente(id);
            if (existente != null) {
                gerenciadorCliente.removerCliente(id);
            }
            
            gerenciadorCliente.cadastrarCliente(cliente);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente salvo com sucesso!");
            carregarTabela();
            handleLimpar();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "ID e CPF devem conter apenas números válidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemover() {
        Cliente selecionado = tblClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum Selecionado", "Por favor, selecione um cliente na tabela para remover.");
            return;
        }

        try {
            gerenciadorCliente.removerCliente(selecionado.getId());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente removido com sucesso!");
            carregarTabela();
            handleLimpar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao remover cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpar() {
        txtId.clear();
        txtNome.clear();
        txtCpf.clear();
        txtEndereco.clear();
        tblClientes.getSelectionModel().clearSelection();
    }

    private void carregarTabela() {
        listaClientes = FXCollections.observableArrayList(gerenciadorCliente.listarTodos());
        tblClientes.setItems(listaClientes);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}