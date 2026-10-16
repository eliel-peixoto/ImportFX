package negocio;

import beans.Pedido;
import dados.IRepositorioPedidos;
import java.util.List;

public class GerenciadorPedido implements IGerenciadorPedido {

    private IRepositorioPedidos repositorio;

    public GerenciadorPedido(IRepositorioPedidos repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void criarPedido(Pedido pedido) throws Exception {
        if (pedido == null) {
            throw new Exception("O pedido não pode ser nulo.");
        }
        if (pedido.getCliente() == null) {
            throw new Exception("Não é possível criar um pedido sem um cliente associado.");
        }
        //Verifica se a lista de itens existe e não está vazia
        if (pedido.getItemPedido() == null || pedido.getItemPedido().isEmpty()) {
            throw new Exception("O pedido deve conter pelo menos um item.");
        }

        repositorio.cadastrar(pedido);
    }

    @Override
    public Pedido buscarPedido(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Pedido> listarPedidos() {
        return repositorio.listarTodos();
    }

    @Override
    public void atualizarStatusPedido(Pedido pedido) throws Exception {
        if (pedido == null || pedido.getId() == null) {
            throw new Exception("Pedido inválido.");
        }
        Pedido pedidoExistente = buscarPedido(pedido.getId());
        if (pedidoExistente == null) {
            throw new Exception("Pedido não encontrado no sistema.");
        }
        
        //Atualiza apenas o status e outros dados permitidos
        pedidoExistente.setStatus(pedido.getStatus());
        repositorio.atualizar(pedidoExistente);
    }

    @Override
    public void cancelarPedido(Long id) throws Exception {
        Pedido pedido = buscarPedido(id);
        if (pedido == null) {
            throw new Exception("Pedido não encontrado.");
        }
        repositorio.remover(id);
    }
}