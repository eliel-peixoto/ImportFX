package negocio;

import beans.Fornecedor;
import java.util.List;

public interface IGerenciadorFornecedor {
    void cadastrarFornecedor(Fornecedor fornecedor) throws Exception;
    List<Fornecedor> listarTodos();
    Fornecedor buscarFornecedor(long id);
    void removerFornecedor(long id) throws Exception;
}