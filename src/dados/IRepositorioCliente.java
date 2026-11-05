package dados;

import beans.Cliente;
import java.util.List;

public interface IRepositorioCliente {
    void cadastrar(Cliente cliente);
    List<Cliente> listarTodos();
    Cliente buscarPorId(long id);
    void remover(long id);
}