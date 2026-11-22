package dados;

import beans.Cliente;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class RepositorioCliente extends RepositorioGenerico<Cliente> implements IRepositorioCliente {

    private static final String ARQUIVO = "clientes.json";

    public RepositorioCliente() {
        super();
        Type tipoLista = new TypeToken<List<Cliente>>() {}.getType();
        this.elementos.addAll(PersistenciaJson.carregarLista(ARQUIVO, tipoLista));

        long maiorId = 0;
        for (Cliente c : this.elementos) {
            if (c.getId() != null && c.getId() > maiorId) {
                maiorId = c.getId();
            }
        }
        Cliente.sincronizarSequenciaId(maiorId);
    }

    public void salvar() {
        PersistenciaJson.salvarLista(ARQUIVO, this.elementos);
    }

    @Override
    public void cadastrar(Cliente cliente) {
        if (cliente == null) {
            return;
        }
        cliente.gerarId();
        super.cadastrar(cliente);
        salvar();
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
                salvar();
            }
        }
    }

    @Override
    public void remover(long id) {
        Cliente c = buscarPorId(id);
        if (c != null) {
            super.removerElemento(c);
            salvar();
        }
    }
}