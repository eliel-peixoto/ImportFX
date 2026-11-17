package negocio;

import beans.Fornecedor;
import beans.Moeda;
import java.util.List;

public interface IGerenciadorFornecedor {
    void cadastrarFornecedor(Fornecedor fornecedor) throws Exception;
    void atualizarFornecedor(long id, String nome, String paisOrigem, Moeda moedaPadrao) throws Exception;
    List<Fornecedor> listarTodos();
    Fornecedor buscarFornecedor(long id);
    void removerFornecedor(long id) throws Exception;
}