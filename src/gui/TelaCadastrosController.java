package gui;

import beans.Cliente;
import beans.Fornecedor;
import beans.Moeda;
import dados.Repositorios;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import negocio.GerenciadorCliente;
import negocio.GerenciadorFornecedor;
import negocio.IGerenciadorPedido;
import negocio.IGerenciadorRelatorios;

public class TelaCadastrosController implements Initializable {

    // Componentes de Clientes
    @FXML private TextField txtClienteId;
    @FXML private TextField txtClienteNome;
    @FXML private TextField txtClienteCpf;
    @FXML private TextField txtClienteEndereco;
    @FXML private Button btnSalvarCliente;
    @FXML private Button btnRemoverCliente;
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
    @FXML private Button btnSalvarFornecedor;
    @FXML private Button btnRemoverFornecedor;
    @FXML private TableView<Fornecedor> tblFornecedores;
    @FXML private TableColumn<Fornecedor, Long> colFornecedorId;
    @FXML private TableColumn<Fornecedor, String> colFornecedorNome;
    @FXML private TableColumn<Fornecedor, String> colFornecedorPais;
    @FXML private TableColumn<Fornecedor, Moeda> colFornecedorMoeda;

    private GerenciadorCliente gerenciadorCliente;
    private GerenciadorFornecedor gerenciadorFornecedor;

    private boolean modoAdmin = false;
    private IGerenciadorPedido gerenciadorPedidoAdmin;
    private IGerenciadorRelatorios gerenciadorRelatoriosAdmin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gerenciadorCliente = new GerenciadorCliente(Repositorios.clientes());
        gerenciadorFornecedor = GerenciadorFornecedor.getInstance();

        cbFornecedorMoeda.setItems(FXCollections.observableArrayList(Moeda.suportadas()));

        configurarTabelas();
        configurarListenersSelecao();
        atualizarTabelas();
        aplicarPermissoesModo();
    }

    public void configurarModoAdmin(boolean modoAdmin,
                                    IGerenciadorPedido gp,
                                    IGerenciadorRelatorios gr) {
        this.modoAdmin = modoAdmin;
        this.gerenciadorPedidoAdmin = gp;
        this.gerenciadorRelatoriosAdmin = gr;
        aplicarPermissoesModo();
    }

    private void aplicarPermissoesModo() {
        if (btnRemoverCliente != null) {
            btnRemoverCliente.setVisible(modoAdmin);
            btnRemoverCliente.setManaged(modoAdmin);
        }
        if (btnRemoverFornecedor != null) {
            btnRemoverFornecedor.setVisible(modoAdmin);
            btnRemoverFornecedor.setManaged(modoAdmin);
        }
        if (btnSalvarCliente != null) {
            btnSalvarCliente.setText(modoAdmin ? "Salvar / Atualizar Cliente" : "Cadastrar Cliente");
        }
        if (btnSalvarFornecedor != null) {
            btnSalvarFornecedor.setText(modoAdmin ? "Salvar / Atualizar Fornecedor" : "Cadastrar Fornecedor");
        }
    }

    private void configurarTabelas() {
        colClienteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClienteNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colClienteCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colClienteCpf.setCellFactory(col -> new javafx.scene.control.TableCell<Cliente, Long>() {
            @Override
            protected void updateItem(Long cpf, boolean empty) {
                super.updateItem(cpf, empty);
                setText(empty || cpf == null ? null : String.format("%011d", cpf));
            }
        });
        colClienteEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        colFornecedorId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFornecedorNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colFornecedorPais.setCellValueFactory(new PropertyValueFactory<>("paisOrigem"));
        colFornecedorMoeda.setCellValueFactory(new PropertyValueFactory<>("moedaPadrao"));
    }

    private void configurarListenersSelecao() {
        // Apenas administradores podem selecionar registros na tabela para edição/exclusão
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (!modoAdmin) {
                if (selecionado != null) {
                    Platform.runLater(() -> tblClientes.getSelectionModel().clearSelection());
                }
                return;
            }
            if (selecionado != null) {
                txtClienteId.setText(String.valueOf(selecionado.getId()));
                txtClienteNome.setText(selecionado.getNome());
                txtClienteCpf.setText(String.format("%011d", selecionado.getCpf()));
                txtClienteEndereco.setText(selecionado.getEndereco());
                bloquearEdicaoCpf(true);
            } else {
                bloquearEdicaoCpf(false);
            }
        });

        tblFornecedores.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (!modoAdmin) {
                if (selecionado != null) {
                    Platform.runLater(() -> tblFornecedores.getSelectionModel().clearSelection());
                }
                return;
            }
            if (selecionado != null) {
                txtFornecedorId.setText(String.valueOf(selecionado.getId()));
                txtFornecedorNome.setText(selecionado.getNome());
                txtFornecedorPais.setText(selecionado.getPaisOrigem());
                cbFornecedorMoeda.setValue(selecionado.getMoedaPadrao());
            }
        });
    }

    private void bloquearEdicaoCpf(boolean bloquear) {
        txtClienteCpf.setEditable(!bloquear);
        txtClienteCpf.setFocusTraversable(!bloquear);
        if (bloquear) {
            txtClienteCpf.setStyle("-fx-background-color: #eeeeee; -fx-border-color: #cccccc;");
        } else {
            txtClienteCpf.setStyle("-fx-border-color: #ff4501;");
        }
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
            String nome = txtClienteNome.getText().trim();
            String cpfText = txtClienteCpf.getText().trim();
            String endereco = txtClienteEndereco.getText().trim();

            Cliente selecionado = tblClientes.getSelectionModel().getSelectedItem();

            if (selecionado != null && !modoAdmin) {
                mostrarAlerta(Alert.AlertType.WARNING, "Acesso Restrito", "Apenas administradores podem alterar clientes cadastrados.");
                handleLimparCliente(null);
                return;
            }

            if (selecionado == null) {
                if (nome.isEmpty() || cpfText.isEmpty() || endereco.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha nome, CPF e endereço do cliente.");
                    return;
                }
                if (!cpfText.matches("\\d{11}")) {
                    mostrarAlerta(Alert.AlertType.WARNING, "CPF Inválido", "O CPF deve conter exatamente 11 dígitos numéricos.");
                    return;
                }

                long cpf = Long.parseLong(cpfText);
                Cliente novoCliente = new Cliente(nome, cpf, endereco);
                gerenciadorCliente.cadastrarCliente(novoCliente);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente cadastrado com ID " + novoCliente.getId() + "!");
            } else {
                if (nome.isEmpty() || endereco.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha nome e endereço do cliente.");
                    return;
                }

                gerenciadorCliente.atualizarCliente(selecionado.getId(), nome, endereco);
                tblClientes.refresh();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente atualizado com sucesso!");
            }

            handleLimparCliente(null);
            atualizarTabelas();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoverCliente(ActionEvent event) {
        if (!modoAdmin) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acesso Restrito", "Apenas administradores podem excluir clientes.");
            return;
        }

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
        bloquearEdicaoCpf(false);
    }

    // --- AÇÕES DE FORNECEDOR ---
    @FXML
    private void handleSalvarFornecedor(ActionEvent event) {
        try {
            String nome = txtFornecedorNome.getText().trim();
            String paisOrigem = txtFornecedorPais.getText().trim();
            Moeda moedaPadrao = cbFornecedorMoeda.getValue();

            if (nome.isEmpty() || paisOrigem.isEmpty() || moedaPadrao == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha nome, país de origem e moeda padrão.");
                return;
            }

            Fornecedor selecionado = tblFornecedores.getSelectionModel().getSelectedItem();

            if (selecionado != null && !modoAdmin) {
                mostrarAlerta(Alert.AlertType.WARNING, "Acesso Restrito", "Apenas administradores podem alterar fornecedores cadastrados.");
                handleLimparFornecedor(null);
                return;
            }

            if (selecionado == null) {
                Fornecedor novoFornecedor = new Fornecedor(nome, paisOrigem, moedaPadrao);
                gerenciadorFornecedor.cadastrarFornecedor(novoFornecedor);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor cadastrado com ID " + novoFornecedor.getId() + "!");
            } else {
                gerenciadorFornecedor.atualizarFornecedor(selecionado.getId(), nome, paisOrigem, moedaPadrao);
                tblFornecedores.refresh();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor atualizado com sucesso!");
            }

            handleLimparFornecedor(null);
            atualizarTabelas();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar fornecedor: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoverFornecedor(ActionEvent event) {
        if (!modoAdmin) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acesso Restrito", "Apenas administradores podem excluir fornecedores.");
            return;
        }

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
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            if (modoAdmin) {
                TelaAdminController.abrirParaAdmin(stage, gerenciadorPedidoAdmin, gerenciadorRelatoriosAdmin);
                return;
            }
            Parent root = FXMLLoader.load(getClass().getResource("/gui/TelaLogin.fxml"));
            stage.setTitle("ImportFX - Acesso ao Sistema");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
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