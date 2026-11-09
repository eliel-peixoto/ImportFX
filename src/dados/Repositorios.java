package dados;
 
/**
 * Guarda UMA instância compartilhada de cada repositório.
 * Sem isso, cada tela criava seu próprio "new Repositorio...()" e os dados
 * cadastrados numa tela sumiam ao trocar de tela.
 * Todas as telas (produto, fornecedor, pedido...) devem usar estes métodos.
 */
public final class Repositorios {
 
	//colocar os outros repositorios
    private static final RepositorioProdutos PRODUTOS = new RepositorioProdutos();
    private static final RepositorioFornecedores FORNECEDORES = new RepositorioFornecedores();
    private static final RepositorioPedidos PEDIDOS = new RepositorioPedidos();
    private static final RepositorioCotacoes COTACOES = new RepositorioCotacoes();
 
    private Repositorios() { }
 
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
