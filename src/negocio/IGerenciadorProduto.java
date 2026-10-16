package negocio;

import beans.Produto;
import java.util.List;

public interface IGerenciadorProduto {
    void cadastrarProduto(Produto produto) throws Exception;
    Produto buscarProduto(long id);
    List<Produto> listarTodos();
    void atualizarProduto(Produto produto) throws Exception;
    void removerProduto(long id) throws Exception;
}