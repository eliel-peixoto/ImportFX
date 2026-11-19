package gui;

import beans.Cliente;
import beans.Fornecedor;
import dados.ClientePtax;
import dados.Repositorios;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import negocio.GerenciadorCambio;
import negocio.GerenciadorPedido;
import negocio.GerenciadorProduto;
import negocio.GerenciadorRelatorios;

public class TelaLoginController {

    @FXML private ComboBox<Cliente> cbClientes;
    @FXML private ComboBox<Fornecedor> cbFornecedores;
    @FXML private Label lblAvisoLogin;

    @FXML
    public void initialize() {
        configurarComboBoxes();
        carregarPerfis();
    }

    private void configurarComboBoxes() {
        cbClientes.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                if (c == null) return "";
                String cpf = c.getCpf() != null ? String.format("%011d", c.getCpf()) : "-";
                return "ID " + c.getId() + " - " + c.getNome() + " (CPF: " + cpf + ")";
            }

            @Override
            public Cliente fromString(String s) {
                return null;
            }
        });

        cbFornecedores.setConverter(new StringConverter<Fornecedor>() {
            @Override
            public String toString(Fornecedor f) {
                if (f == null) return "";
                return "ID " + f.getId() + " - " + f.getNome() + " (" + f.getPaisOrigem() + ")";
            }

            @Override
            public Fornecedor fromString(String s) {
                return null;
            }
        });
    }

    private void carregarPerfis() {
        cbClientes.setItems(FXCollections.observableArrayList(Repositorios.clientes().listarTodos()));
        cbFornecedores.setItems(FXCollections.observableArrayList(Repositorios.fornecedores().listarTodos()));

        if (cbClientes.getItems().isEmpty() && cbFornecedores.getItems().isEmpty()) {
            lblAvisoLogin.setText("Nenhum cliente ou fornecedor cadastrado ainda. Use o botão verde acima para cadastrar.");
        } else {
            lblAvisoLogin.setText("");
        }
    }

    @FXML
    private void handleEntrarCliente(ActionEvent event) {
        Cliente selecionado = cbClientes.getValue();
        if (selecionado == null) {
            lblAvisoLogin.setText("Selecione um cliente na lista antes de entrar.");
            return;
        }
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            TelaClienteController.abrirParaCliente(
                    stage,
                    selecionado,
                    GerenciadorPedido.getInstance(),
                    new GerenciadorCambio(new ClientePtax(), Repositorios.cotacoes()),
                    new GerenciadorRelatorios(
                            Repositorios.produtos(),
                            Repositorios.clientes(),
                            Repositorios.fornecedores(),
                            Repositorios.pedidos())
            );
        } catch (Exception e) {
            lblAvisoLogin.setText("Erro ao abrir área do cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleEntrarFornecedor(ActionEvent event) {
        Fornecedor selecionado = cbFornecedores.getValue();
        if (selecionado == null) {
            lblAvisoLogin.setText("Selecione um fornecedor na lista antes de entrar.");
            return;
        }
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            TelaFornecedorController.abrirParaFornecedor(
                    stage,
                    selecionado,
                    GerenciadorPedido.getInstance(),
                    GerenciadorProduto.getInstance()
            );
        } catch (Exception e) {
            lblAvisoLogin.setText("Erro ao abrir área do fornecedor: " + e.getMessage());
        }
    }

    @FXML
    private void handleEntrarAdmin(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            TelaAdminController.abrirParaAdmin(
                    stage,
                    GerenciadorPedido.getInstance(),
                    new GerenciadorRelatorios(
                            Repositorios.produtos(),
                            Repositorios.clientes(),
                            Repositorios.fornecedores(),
                            Repositorios.pedidos())
            );
        } catch (Exception e) {
            lblAvisoLogin.setText("Erro ao abrir painel do administrador: " + e.getMessage());
        }
    }

    @FXML
    private void handleAbrirCadastros(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/TelaCadastros.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("ImportFX - Gestão de Cadastros");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            lblAvisoLogin.setText("Erro ao abrir cadastros: " + e.getMessage());
        }
    }
}