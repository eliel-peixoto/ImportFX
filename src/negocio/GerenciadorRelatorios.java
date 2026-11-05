package negocio;

import beans.*;
import dados.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorRelatorios implements IGerenciadorRelatorios {
    private IRepositorioProdutos repoProdutos;
    private IRepositorioCliente repoClientes;
    private IRepositorioFornecedores repoFornecedores;

    public GerenciadorRelatorios(IRepositorioProdutos repoProdutos, 
                                IRepositorioCliente repoClientes, 
                                IRepositorioFornecedores repoFornecedores) {
        this.repoProdutos = repoProdutos;
        this.repoClientes = repoClientes;
        this.repoFornecedores = repoFornecedores;
    }

    @Override
    public List<Produto> relatorioProdutosPorCategoria(Class<?> categoria) {
        List<Produto> filtrados = new ArrayList<>();
        for (Produto p : repoProdutos.listarTodos()) {
            if (categoria.isInstance(p)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    @Override
    public List<Fornecedor> relatorioFornecedoresPorMoeda(Moeda moeda) {
        List<Fornecedor> filtrados = new ArrayList<>();
        for (Fornecedor f : repoFornecedores.listarTodos()) {
            if (f.getMoedaPadrao() == moeda) {
                filtrados.add(f);
            }
        }
        return filtrados;
    }

    @Override
    public List<Cliente> relatorioClientesPorNome(String termo) {
        List<Cliente> filtrados = new ArrayList<>();
        for (Cliente c : repoClientes.listarTodos()) {
            if (c.getNome() != null && c.getNome().toLowerCase().contains(termo.toLowerCase())) {
                filtrados.add(c);
            }
        }
        return filtrados;
    }

    @Override
    public double calcularValorTotalProdutos() {
        double total = 0.0;
        for (Produto p : repoProdutos.listarTodos()) {
            total += p.getPreco();
        }
        return total;
    }
}