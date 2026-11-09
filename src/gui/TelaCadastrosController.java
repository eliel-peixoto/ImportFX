package gui;

import beans.Cliente;
import beans.Fornecedor;
import beans.Moeda;
import dados.RepositorioCliente;
import dados.RepositorioFornecedores;
import negocio.GerenciadorCliente;
import negocio.GerenciadorFornecedor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class TelaCadastrosController implements Initializable {

    // Componentes de Clientes
    @FXML private TextField txtClienteId;
    @FXML private TextField txtClienteNome;
    @FXML private TextField txtClienteCpf;
    @FXML private TextField txtClienteEndereco;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Long> colClienteId;
    @FXML private TableColumn<Cliente, String> colClienteNome;
    @FXML private TableColumn<Cliente, Long> colClienteCpf;
    @FXML private TableColumn<Cliente, String> colClienteEndereco;

    // Componentes de Fornecedores
    @FXML private TextField txtFornecedorId;
    @FXML private TextField txtFornecedorNome;
    @FXML private TextField txtFornecedorPais;
    @FXML private ComboBox<Moeda> cbFornecedorMoeda;
    @FXML private TableView<Fornecedor> tblFornecedores;
    @FXML private TableColumn<Fornecedor, Long> colFornecedorId;
    @FXML private TableColumn<Fornecedor, String> colFornecedorNome;
    @FXML private TableColumn<Fornecedor, String> colFornecedorPais;
    @FXML private TableColumn<Fornecedor, Moeda> colFornecedorMoeda;

    private GerenciadorCliente gerenciadorCliente;
    private GerenciadorFornecedor gerenciadorFornecedor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gerenciadorCliente = new GerenciadorCliente(new RepositorioCliente());
        gerenciadorFornecedor = new GerenciadorFornecedor(new RepositorioFornecedores());

        cbFornecedorMoeda.setItems(FXCollections.observableArrayList(Moeda.suportadas()));

        configurarTabelas();
        configurarListenersSelecao();
        atualizarTabelas();
    }

    private void configurarTabelas() {
        colClienteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClienteNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colClienteCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colClienteEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        colFornecedorId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFornecedorNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colFornecedorPais.setCellValueFactory(new PropertyValueFactory<>("paisOrigem"));
        colFornecedorMoeda.setCellValueFactory(new PropertyValueFactory<>("moedaPadrao"));
    }

    private void configurarListenersSelecao() {
        // Seleção na tabela de Clientes preenche os campos
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                txtClienteId.setText(String.valueOf(selecionado.getId()));
                txtClienteNome.setText(selecionado.getNome());
                txtClienteCpf.setText(String.valueOf(selecionado.getCpf()));
                txtClienteEndereco.setText(selecionado.getEndereco());
            }
        });

        // Seleção na tabela de Fornecedores preenche os campos
        tblFornecedores.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                txtFornecedorId.setText(String.valueOf(selecionado.getId()));
                txtFornecedorNome.setText(selecionado.getNome());
                txtFornecedorPais.setText(selecionado.getPaisOrigem());
                cbFornecedorMoeda.setValue(selecionado.getMoedaPadrao());
            }
        });
    }

    private void atualizarTabelas() {
        ObservableList<Cliente> listClientes = FXCollections.observableArrayList(gerenciadorCliente.listarTodos());
        tblClientes.setItems(listClientes);

        ObservableList<Fornecedor> listFornecedores = FXCollections.observableArrayList(gerenciadorFornecedor.listarTodos());
        tblFornecedores.setItems(listFornecedores);
    }

    // --- AÇÕES DE CLIENTE ---
    @FXML
    private void handleSalvarCliente(ActionEvent event) {
        try {
            String idText = txtClienteId.getText().trim();
            String nome = txtClienteNome.getText().trim();
            String cpfText = txtClienteCpf.getText().trim();
            String endereco = txtClienteEndereco.getText().trim();

            if (idText.isEmpty() || nome.isEmpty() || cpfText.isEmpty() || endereco.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha todos os campos do cliente.");
                return;
            }

            // Validação: verifica se contém exatamente 11 dígitos numéricos
            if (!cpfText.matches("\\d{11}")) {
                mostrarAlerta(Alert.AlertType.WARNING, "CPF Inválido", "O CPF deve conter exatamente 11 dígitos numéricos.");
                return;
            }

            Long id = Long.valueOf(idText);
            Long cpf = Long.valueOf(cpfText);

            Cliente cliente = new Cliente(id, nome, cpf, endereco);

            Cliente existente = gerenciadorCliente.buscarCliente(id);
            if (existente != null) {
                gerenciadorCliente.removerCliente(id);
            }

            gerenciadorCliente.cadastrarCliente(cliente);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente salvo com sucesso!");

            handleLimparCliente(null);
            atualizarTabelas();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "ID e CPF devem conter apenas números válidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoverCliente(ActionEvent event) {
        Cliente selecionado = tblClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum Selecionado", "Por favor, selecione um cliente na tabela para remover.");
            return;
        }

        try {
            gerenciadorCliente.removerCliente(selecionado.getId());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente removido com sucesso!");
            handleLimparCliente(null);
            atualizarTabelas();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao remover cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimparCliente(ActionEvent event) {
        txtClienteId.clear();
        txtClienteNome.clear();
        txtClienteCpf.clear();
        txtClienteEndereco.clear();
        tblClientes.getSelectionModel().clearSelection();
    }

    // --- AÇÕES DE FORNECEDOR ---
    @FXML
    private void handleSalvarFornecedor(ActionEvent event) {
        try {
            if (txtFornecedorId.getText().trim().isEmpty() || txtFornecedorNome.getText().trim().isEmpty() ||
                    txtFornecedorPais.getText().trim().isEmpty() || cbFornecedorMoeda.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha todos os campos do formulário.");
                return;
            }

            Long id = Long.valueOf(txtFornecedorId.getText().trim());
            String nome = txtFornecedorNome.getText().trim();
            String paisOrigem = txtFornecedorPais.getText().trim();
            Moeda moedaPadrao = cbFornecedorMoeda.getValue();

            Fornecedor fornecedor = new Fornecedor(id, nome, paisOrigem, moedaPadrao);

            Fornecedor existente = gerenciadorFornecedor.buscarFornecedor(id);
            if (existente != null) {
                gerenciadorFornecedor.removerFornecedor(id);
            }

            gerenciadorFornecedor.cadastrarFornecedor(fornecedor);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor salvo com sucesso!");

            handleLimparFornecedor(null);
            atualizarTabelas();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "O campo ID deve ser um número inteiro válido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar fornecedor: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoverFornecedor(ActionEvent event) {
        Fornecedor selecionado = tblFornecedores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum Selecionado", "Por favor, selecione um fornecedor na tabela para remover.");
            return;
        }

        try {
            gerenciadorFornecedor.removerFornecedor(selecionado.getId());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor removido com sucesso!");
            handleLimparFornecedor(null);
            atualizarTabelas();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao remover fornecedor: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimparFornecedor(ActionEvent event) {
        txtFornecedorId.clear();
        txtFornecedorNome.clear();
        txtFornecedorPais.clear();
        cbFornecedorMoeda.setValue(null);
        tblFornecedores.getSelectionModel().clearSelection();
    }

    // --- NAVEGAÇÃO ---
    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/MenuPrincipal.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}