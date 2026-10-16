package negocio;

import beans.Pedido;
import java.util.List;

public interface IGerenciadorPedido {
    void criarPedido(Pedido pedido) throws Exception;
    Pedido buscarPedido(Long id);
    List<Pedido> listarPedidos();
    void atualizarStatusPedido(Pedido pedido) throws Exception;
    void cancelarPedido(Long id) throws Exception;
}