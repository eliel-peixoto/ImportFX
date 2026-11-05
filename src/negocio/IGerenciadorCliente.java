package negocio;

import beans.Cliente;
import java.util.List;

public interface IGerenciadorCliente {
    void cadastrarCliente(Cliente cliente) throws Exception;
    List<Cliente> listarTodos();
    Cliente buscarCliente(long id);
    void removerCliente(long id) throws Exception;
}