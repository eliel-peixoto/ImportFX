package gui;

import beans.Brinquedo;
import beans.Eletronico;
import beans.Fornecedor;
import beans.Produto;
import beans.Vestuario;
import dados.Repositorios;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import negocio.GerenciadorFornecedor;
import negocio.GerenciadorProduto;

public class TelaProdutoController {

    @FXML private ComboBox<String> cbCategoria;
    @FXML private TextField txtNomeProduto;
    @FXML private TextField txtPreco;
    @FXML private TextField txtPeso;
    @FXML private TextField txtAliquota;
    @FXML private Spinner<Integer> spnQuantidade;
    @FXML private Label msgErroProduto;

    @FXML private ComboBox<Fornecedor> cbFornecedores;
    @FXML private Label msgErroFornecedor;
    @FXML private Text msgSucesso;
    @FXML private Button botaoConfirmar;

    private final GerenciadorProduto gerenciadorProduto = new GerenciadorProduto(Repositorios.produtos());
    private final GerenciadorFornecedor gerenciadorFornecedor = new GerenciadorFornecedor(Repositorios.fornecedores());

    private boolean modoModalFornecedor = false;

    @FXML
    public void initialize() {
        spnQuantidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        cbCategoria.setItems(FXCollections.observableArrayList("Vestuário", "Eletrônico", "Brinquedo"));

        configurarComboFornecedores();

        msgErroProduto.setText("");
        msgSucesso.setText("");

        List<Fornecedor> fornecedores = gerenciadorFornecedor.listarTodos();
        cbFornecedores.setItems(FXCollections.observableArrayList(fornecedores));

        if (fornecedores.isEmpty()) {
            msgErroFornecedor.setText("Nenhum fornecedor cadastrado. Cadastre um fornecedor na tela de Cadastro primeiro.");
            if (botaoConfirmar != null) {
                botaoConfirmar.setDisable(true);
            }
        } else {
            msgErroFornecedor.setText("");
        }
    }

    public void configurarParaFornecedor(Fornecedor fornecedor) {
        this.modoModalFornecedor = true;
        if (fornecedor != null) {
            if (!cbFornecedores.getItems().contains(fornecedor)) {
                cbFornecedores.getItems().add(fornecedor);
            }
            cbFornecedores.setValue(fornecedor);
            cbFornecedores.setDisable(true);
            msgErroFornecedor.setText("");
            if (botaoConfirmar != null) {
                botaoConfirmar.setDisable(false);
            }
        }
    }

    private void configurarComboFornecedores() {
        cbFornecedores.setConverter(new StringConverter<Fornecedor>() {
            @Override
            public String toString(Fornecedor fornecedor) {
                if (fornecedor == null) return "";
                return fornecedor.getNome() + " (" + fornecedor.getPaisOrigem() + " - " + fornecedor.getMoedaPadrao() + ")";
            }

            @Override
            public Fornecedor fromString(String string) {
                return null;
            }
        });

        cbFornecedores.valueProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                msgErroFornecedor.setText("");
                txtPreco.setPromptText("Digite o preço em " + selecionado.getMoedaPadrao());
            } else {
                txtPreco.setPromptText("Digite o preço na moeda de origem");
            }
        });
    }

    @FXML
    private void handleConfirmar(ActionEvent event) {
        msgErroProduto.setText("");
        msgErroFornecedor.setText("");
        msgSucesso.setText("");

        try {
            if (cbCategoria.getValue() == null) {
                msgErroProduto.setText("Selecione uma categoria.");
                return;
            }
            Fornecedor fornecedor = cbFornecedores.getValue();
            if (fornecedor == null) {
                msgErroFornecedor.setText("Selecione um fornecedor.");
                return;
            }
            String nome = txtNomeProduto.getText().trim();
            if (nome.isEmpty()) {
                msgErroProduto.setText("Informe o nome do produto.");
                return;
            }

            double preco = lerNumero(txtPreco.getText(), "Preço", true);
            double peso = lerNumero(txtPeso.getText(), "Peso", true);
            double aliquota = lerNumero(txtAliquota.getText(), "Alíquota", false);
            int quantidadeEstoque = (spnQuantidade != null && spnQuantidade.getValue() != null)
                    ? spnQuantidade.getValue() : 1;

            long id = gerarId(fornecedor);
            Produto novo;
            switch (cbCategoria.getValue()) {
                case "Vestuário":  novo = new Vestuario(id, nome, preco, peso, aliquota); break;
                case "Eletrônico": novo = new Eletronico(id, nome, preco, peso, aliquota); break;
                default:           novo = new Brinquedo(id, nome, preco, peso, aliquota); break;
            }
            novo.setIdFornecedor(fornecedor.getId());
            novo.setQuantidadeEstoque(quantidadeEstoque);

            gerenciadorProduto.cadastrarProduto(novo);
            fornecedor.getListaProdutos().cadastrar(novo);

            msgSucesso.setText("Produto \"" + nome + "\" (ID: " + id + " | Estoque: " + quantidadeEstoque + ") cadastrado!");
            txtNomeProduto.clear();
            txtPreco.clear();
            txtPeso.clear();
            txtAliquota.clear();
            spnQuantidade.getValueFactory().setValue(1);

        } catch (Exception e) {
            msgErroProduto.setText(e.getMessage());
        }
    }

    private double lerNumero(String texto, String campo, boolean obrigatorio) throws Exception {
        String t = (texto == null) ? "" : texto.trim().replace(',', '.');
        if (t.isEmpty()) {
            if (obrigatorio) throw new Exception("Informe o campo " + campo + ".");
            return 0.0;
        }
        double valor;
        try {
            valor = Double.parseDouble(t);
        } catch (NumberFormatException e) {
            throw new Exception(campo + " deve ser um número.");
        }
        if (valor < 0) throw new Exception(campo + " não pode ser negativo.");
        return valor;
    }

    private long gerarId(Fornecedor fornecedor) {
        long id = System.currentTimeMillis();
        while (fornecedor.getListaProdutos().buscarPorId(id) != null
                || gerenciadorProduto.buscarProduto(id) != null) {
            id++;
        }
        return id;
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (modoModalFornecedor) {
            stage.close();
            return;
        }
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/MenuPrincipal.fxml"));
            stage.setTitle("ImportFX - Menu Principal");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}