package negocio;

import beans.Cliente;
import java.util.List;

public interface IGerenciadorCliente {
    void cadastrarCliente(Cliente cliente) throws Exception;
    void atualizarCliente(long id, String nome, String endereco) throws Exception;
    void atualizarCliente(long id, String nome, Long cpf, String endereco) throws Exception;
    List<Cliente> listarTodos();
    Cliente buscarCliente(long id);
    void removerCliente(long id) throws Exception;
}