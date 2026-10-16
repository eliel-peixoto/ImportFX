package dados;

import beans.Pedido;
import java.util.List;

public interface IRepositorioPedidos {
    void cadastrar(Pedido pedido);
    Pedido buscarPorId(Long id);
    List<Pedido> listarTodos();
    void atualizar(Pedido pedido);
    void remover(Long id);
}