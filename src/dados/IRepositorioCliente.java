package dados;

import beans.Cliente;
import java.util.List;

public interface IRepositorioCliente {
    void cadastrar(Cliente cliente);
    Cliente buscarPorCpf(Long cpf);
    List<Cliente> listarTodos();
    void atualizar(Cliente cliente);
    void remover(Long cpf);
}