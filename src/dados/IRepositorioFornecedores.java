package dados;

import beans.Fornecedor;
import java.util.List;

public interface IRepositorioFornecedores {
    void cadastrar(Fornecedor fornecedor);
    List<Fornecedor> listarTodos();
    Fornecedor buscarPorId(long id);
    void remover(long id);
}