package dados;

import java.util.List;
import beans.Fornecedor;

public interface IRepositorioFornecedores {
    void adicionarFornecedor(Fornecedor f);
    Fornecedor procurarFornecedor(int id, String nome);
    void removerFornecedor(int id, String nome);
    List<Fornecedor> listarTodos();
}