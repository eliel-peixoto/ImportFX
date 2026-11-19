package negocio;

import beans.Produto;
import dados.IRepositorioProdutos;
import dados.Repositorios;
import java.util.List;

public class GerenciadorProduto implements IGerenciadorProduto {

    private static GerenciadorProduto instance;
    private final IRepositorioProdutos repositorio;

    private GerenciadorProduto(IRepositorioProdutos repositorio) {
        this.repositorio = repositorio;
    }

    public static synchronized GerenciadorProduto getInstance() {
        if (instance == null) {
            instance = new GerenciadorProduto(Repositorios.produtos());
        }
        return instance;
    }

    @Override
    public void cadastrarProduto(Produto produto) throws Exception {
        if (produto == null) {
            throw new Exception("O produto não pode ser nulo.");
        }
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new Exception("O nome do produto é obrigatório.");
        }
        if (produto.getIdFornecedor() == null) {
            throw new Exception("O produto deve estar vinculado a um fornecedor.");
        }
        repositorio.cadastrar(produto);
    }

    @Override
    public Produto buscarProduto(long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Produto> listarTodos() {
        return repositorio.listarTodos();
    }

    @Override
    public void atualizarProduto(Produto produto) throws Exception {
        if (produto == null) {
            throw new Exception("Produto inválido para atualização.");
        }
        if (buscarProduto(produto.getId()) == null) {
            throw new Exception("Produto não encontrado no sistema.");
        }
        repositorio.atualizar(produto);
    }

    @Override
    public void removerProduto(long id) throws Exception {
        if (buscarProduto(id) == null) {
            throw new Exception("Produto não encontrado para remoção.");
        }
        repositorio.remover(id);
    }
}