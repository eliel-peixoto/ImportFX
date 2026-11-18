package negocio;

import beans.Pedido;
import java.io.File;
import java.util.List;

public interface IGerenciadorPedido {
    Long gerarProximoId();
    void criarPedido(Pedido pedido) throws Exception;
    Pedido buscarPedido(Long id);
    List<Pedido> listarPedidos();
    void atualizarStatusPedido(Pedido pedido) throws Exception;
    void cancelarPedido(Long id) throws Exception;
    double calcularFreteInternacional(Pedido pedido);
    double calcularTotalImpostos(Pedido pedido);
    double calcularTotalFinal(Pedido pedido);
    void exportarPedidoParaHtml(Long id, String caminhoPasta) throws Exception;
    File exportarPedidoParaPdf(Long id, String caminhoPasta) throws Exception;
}