package dados;

import beans.Cliente;
import beans.Fornecedor;
import beans.Pedido;
import beans.Produto;
import java.util.Objects;

public final class Repositorios {

    private static final RepositorioCliente CLIENTES = new RepositorioCliente();
    private static final RepositorioProdutos PRODUTOS = new RepositorioProdutos();
    private static final RepositorioFornecedores FORNECEDORES = new RepositorioFornecedores();
    private static final RepositorioPedidos PEDIDOS = new RepositorioPedidos();
    private static final RepositorioCotacoes COTACOES = new RepositorioCotacoes();

    static {
        sincronizarReferencias();
    }

    private Repositorios() {
    }

    private static void sincronizarReferencias() {
        //Reconecta produtos à lista interna de cada fornecedor
        for (Produto prod : PRODUTOS.listarTodos()) {
            if (prod.getIdFornecedor() != null) {
                Fornecedor forn = FORNECEDORES.buscarPorId(prod.getIdFornecedor());
                if (forn != null && forn.getListaProdutos().buscarPorId(prod.getId()) == null) {
                    forn.getListaProdutos().cadastrar(prod);
                }
            }
        }

        //Reconecta cliente e fornecedor reais de cada pedido
        for (Pedido ped : PEDIDOS.listarTodos()) {
            if (ped.getCliente() != null && ped.getCliente().getId() != null) {
                Cliente cliReal = CLIENTES.buscarPorId(ped.getCliente().getId());
                if (cliReal != null) {
                    ped.setCliente(cliReal);
                }
            }
            if (ped.getFornecedor() != null && ped.getFornecedor().getId() != null) {
                Fornecedor fornReal = FORNECEDORES.buscarPorId(ped.getFornecedor().getId());
                if (fornReal != null && !Objects.equals(ped.getFornecedor(), fornReal)) {
                    ped.setFornecedor(fornReal);
                }
            }
        }
    }

    public static RepositorioCliente clientes() {
        return CLIENTES;
    }

    public static RepositorioProdutos produtos() {
        return PRODUTOS;
    }

    public static RepositorioFornecedores fornecedores() {
        return FORNECEDORES;
    }

    public static RepositorioPedidos pedidos() {
        return PEDIDOS;
    }

    public static RepositorioCotacoes cotacoes() {
        return COTACOES;
    }
}