package dados;

import beans.Produto;
import java.util.List;

public interface IRepositorioProdutos {
    void cadastrar(Produto produto);
    Produto buscarPorId(long id);
    List<Produto> listarTodos();
    void atualizar(Produto produto);
    void remover(long id);
}