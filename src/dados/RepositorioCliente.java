package dados;

import beans.Cliente;
import java.util.List;

public class RepositorioCliente extends RepositorioGenerico<Cliente> implements IRepositorioCliente {

    public RepositorioCliente() {
        super();
    }

    @Override
    public void cadastrar(Cliente cliente) {
        if (cliente != null) {
            cliente.gerarId();
        }
        super.cadastrar(cliente);
    }

    @Override
    public List<Cliente> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public Cliente buscarPorId(long id) {
        for (Cliente c : elementos) {
            if (c.getId() != null && c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Cliente clienteAtualizado) {
        if (clienteAtualizado != null && clienteAtualizado.getId() != null) {
            Cliente antigo = buscarPorId(clienteAtualizado.getId());
            if (antigo != null) {
                antigo.setNome(clienteAtualizado.getNome());
                antigo.setEndereco(clienteAtualizado.getEndereco());
            }
        }
    }

    @Override
    public void remover(long id) {
        Cliente c = buscarPorId(id);
        if (c != null) {
            super.removerElemento(c);
        }
    }
}