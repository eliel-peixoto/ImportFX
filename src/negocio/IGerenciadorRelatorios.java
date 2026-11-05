package negocio;

import beans.Cliente;
import beans.Fornecedor;
import beans.Moeda;
import beans.Produto;
import java.util.List;

public interface IGerenciadorRelatorios {
    List<Produto> relatorioProdutosPorCategoria(Class<?> categoria);
    List<Fornecedor> relatorioFornecedoresPorMoeda(Moeda moeda);
    List<Cliente> relatorioClientesPorNome(String termo);
    double calcularValorTotalProdutos();
}