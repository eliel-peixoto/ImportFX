package negocio;

import beans.Cliente;
import beans.Fornecedor;
import beans.Moeda;
import beans.Produto;
import beans.RelatorioPedido;
import beans.RelatorioProduto;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface IGerenciadorRelatorios {
    List<Produto> relatorioProdutosPorCategoria(Class<?> categoria);
    List<Fornecedor> relatorioFornecedoresPorMoeda(Moeda moeda);
    List<Cliente> relatorioClientesPorNome(String termo);
    double calcularValorTotalProdutos();
    List<RelatorioPedido> relatorioPedidosPorPeriodo(LocalDate inicio, LocalDate fim);
    List<RelatorioProduto> relatorioProdutosMaisImportados(LocalDate inicio, LocalDate fim, String categoria);
    void exportarPedidosCsv(List<RelatorioPedido> linhas, Path destino) throws IOException;
    void exportarProdutosCsv(List<RelatorioProduto> linhas, Path destino) throws IOException;
}
