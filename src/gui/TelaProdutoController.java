/*package gui;

import beans.Brinquedo;
import beans.Eletronico;
import beans.Fornecedor;
import beans.Produto;
import beans.Vestuario;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// Importações simuladas dos repositórios e gerenciadores
import dados.RepositorioFornecedores;
import dados.RepositorioProdutos;
import negocio.GerenciadorFornecedor;
import negocio.GerenciadorProduto;

import java.io.IOException;

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

    // Gerenciadores
    private GerenciadorProduto gerenciadorProduto = new GerenciadorProduto(new RepositorioProdutos());
    private GerenciadorFornecedor gerenciadorFornecedor = new GerenciadorFornecedor(new RepositorioFornecedores());

    @FXML
    public void initialize() {
        // Configura o Spinner de quantidade (1 a 100, padrão 1)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spnQuantidade.setValueFactory(valueFactory);

        // Preencher ComboBox de Categorias
        cbCategoria.setItems(FXCollections.observableArrayList("Vestuário", "Eletrônico", "Brinquedo"));

        // Preencher ComboBox de Fornecedores
        // Se a lista de fornecedores estiver salva num arquivo, o gerenciador vai puxar de lá.
        cbFornecedores.setItems(FXCollections.observableArrayList(gerenciadorFornecedor.listarTodos()));

        msgErroProduto.setText("");
        msgErroFornecedor.setText("");
        msgSucesso.setText("");
    }

    @FXML
    private void handleConfirmar(ActionEvent event) {
        msgErroProduto.setText("");
        msgErroFornecedor.setText("");
        msgSucesso.setText("");

        try {
            // Validações básicas
            if (cbCategoria.getValue() == null) {
                msgErroProduto.setText("Selecione uma categoria.");
                return;
            }
            if (txtNomeProduto.getText().trim().isEmpty()) {
                msgErroProduto.setText("Informe o nome do produto.");
                return;
            }
            
            Fornecedor fornecedorSelecionado = cbFornecedores.getValue();
            if (fornecedorSelecionado == null) {
                msgErroFornecedor.setText("Selecione um fornecedor.");
                return;
            }

            // Geração de ID Único baseado no tempo do sistema (Millissegundos)
            long idGerado = System.currentTimeMillis();
            
            String nome = txtNomeProduto.getText();
            double preco = Double.parseDouble(txtPreco.getText());
            double peso = Double.parseDouble(txtPeso.getText());
            double aliquota = Double.parseDouble(txtAliquota.getText());
            int qtd = spnQuantidade.getValue();
            
            String categoria = cbCategoria.getValue();
            Produto novoProduto = null;

            // Instancia o produto correto dependendo da categoria
            if (categoria.equals("Vestuário")) {
                novoProduto = new Vestuario(idGerado, nome, preco, peso, aliquota);
            } else if (categoria.equals("Eletrônico")) {
                novoProduto = new Eletronico(idGerado, nome, preco, peso, aliquota);
            } else if (categoria.equals("Brinquedo")) {
                novoProduto = new Brinquedo(idGerado, nome, preco, peso, aliquota);
            }

            // Cadastra no sistema de produtos
            gerenciadorProduto.cadastrarProduto(novoProduto);
            
            // Adiciona o produto na lista do fornecedor selecionado
            fornecedorSelecionado.getListaProdutos().cadastrar(novoProduto);

            msgSucesso.setText("Produto (ID: " + idGerado + ") cadastrado com sucesso!");

            // Limpa os campos após salvar
            txtNomeProduto.clear();
            txtPreco.clear();
            txtPeso.clear();
            txtAliquota.clear();

        } catch (NumberFormatException e) {
            msgErroProduto.setText("Preço, Peso e Alíquota devem ser números.");
        } catch (Exception e) {
            msgErroProduto.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/menuPrincipal.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("ImportFX - Menu Principal");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/

package gui;
 
import beans.Brinquedo;
import beans.Eletronico;
import beans.Fornecedor;
import beans.Produto;
import beans.Vestuario;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
 
    // Repositórios COMPARTILHADOS entre as telas
    private final GerenciadorProduto gerenciadorProduto = new GerenciadorProduto(Repositorios.produtos());
    private final GerenciadorFornecedor gerenciadorFornecedor = new GerenciadorFornecedor(Repositorios.fornecedores());
 
    @FXML
    public void initialize() {
        spnQuantidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        cbCategoria.setItems(FXCollections.observableArrayList("Vestuário", "Eletrônico", "Brinquedo"));
        cbFornecedores.setItems(FXCollections.observableArrayList(gerenciadorFornecedor.listarTodos()));
 
        msgErroProduto.setText("");
        msgErroFornecedor.setText("");
        msgSucesso.setText("");
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
            double aliquota = lerNumero(txtAliquota.getText(), "Alíquota", false); // vazio = 0
 
            long id = gerarId(fornecedor);
            Produto novo;
            switch (cbCategoria.getValue()) {
                case "Vestuário":  novo = new Vestuario(id, nome, preco, peso, aliquota); break;
                case "Eletrônico": novo = new Eletronico(id, nome, preco, peso, aliquota); break;
                default:           novo = new Brinquedo(id, nome, preco, peso, aliquota); break;
            }
            novo.setIdFornecedor(fornecedor.getId()); // antes ficava null
 
            gerenciadorProduto.cadastrarProduto(novo);
            fornecedor.getListaProdutos().cadastrar(novo);
 
            msgSucesso.setText("Produto (ID: " + id + ") cadastrado com sucesso!");
            txtNomeProduto.clear();
            txtPreco.clear();
            txtPeso.clear();
            txtAliquota.clear();
 
        } catch (Exception e) {
            msgErroProduto.setText(e.getMessage());
        }
    }
 
    /** Aceita vírgula ou ponto e rejeita valores vazios (se obrigatório) ou negativos. */
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
 
    /** ID único: milissegundos, mas avança se já existir esse ID no fornecedor. */
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
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/MenuPrincipal.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("ImportFX - Menu Principal");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
