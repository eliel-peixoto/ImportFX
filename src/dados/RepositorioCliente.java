package dados;

import beans.Cliente;
import java.util.List;

public class RepositorioCliente extends RepositorioGenerico<Cliente> implements IRepositorioCliente {

    public RepositorioCliente() {
        super();
    }

    @Override
    public void cadastrar(Cliente cliente) {
        super.cadastrar(cliente);
    }

    @Override
    public List<Cliente> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public Cliente buscarPorId(long id) {
        for (Cliente c : elementos) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void remover(long id) {
        Cliente c = buscarPorId(id);
        if (c != null) {
            super.removerElemento(c);
        }
    }
}