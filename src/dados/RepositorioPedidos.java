package dados;

import beans.Pedido;
import java.util.List;

public class RepositorioPedidos extends RepositorioGenerico<Pedido> implements IRepositorioPedidos {

    public RepositorioPedidos() {
        super();
    }

    @Override
    public void cadastrar(Pedido pedido) {
        super.cadastrar(pedido);
    }

    @Override
    public List<Pedido> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public Pedido buscarPorId(Long id) {
        for (Pedido p : elementos) {
            // Como id é Long (Objeto), usamos .equals() em vez de ==
            if (p.getId().equals(id)) {
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
                // A lista de itens permanece a mesma ou é manipulada via adicionar/remover do próprio pedido
            }
        }
    }

    @Override
    public void remover(Long id) {
        Pedido p = buscarPorId(id);
        if (p != null) {
            super.removerElemento(p);
        }
    }
}