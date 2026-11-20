package dados;

import beans.Pedido;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class RepositorioPedidos extends RepositorioGenerico<Pedido> implements IRepositorioPedidos {

    private static final String ARQUIVO = "pedidos.json";

    public RepositorioPedidos() {
        super();
        Type tipoLista = new TypeToken<List<Pedido>>() {}.getType();
        this.elementos.addAll(PersistenciaJson.carregarLista(ARQUIVO, tipoLista));
    }

    private void salvar() {
        PersistenciaJson.salvarLista(ARQUIVO, this.elementos);
    }

    @Override
    public void cadastrar(Pedido pedido) {
        super.cadastrar(pedido);
        salvar();
    }

    @Override
    public List<Pedido> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public Pedido buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        for (Pedido p : elementos) {
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Pedido pedido) {
        if (pedido != null && pedido.getId() != null) {
            Pedido antigo = buscarPorId(pedido.getId());
            if (antigo != null) {
                antigo.setCliente(pedido.getCliente());
                antigo.setFornecedor(pedido.getFornecedor());
                antigo.setStatus(pedido.getStatus());
                salvar();
                Repositorios.clientes().salvar();
            }
        }
    }

    @Override
    public void remover(Long id) {
        Pedido p = buscarPorId(id);
        if (p != null) {
            super.removerElemento(p);
            salvar();
        }
    }
}