package dados;

import java.util.List;
import beans.Produto;

public interface IRepositorioProdutos {
    void adicionarProduto(Produto p);
    Produto procurarProduto(int id, String nome);
    void removerProduto(int id, String nome);
    List<Produto> listarTodos();
}
