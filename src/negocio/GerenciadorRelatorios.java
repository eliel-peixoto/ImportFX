package negocio;

import beans.Cliente;
import beans.Fornecedor;
import beans.Item;
import beans.Moeda;
import beans.Pedido;
import beans.Produto;
import beans.RelatorioPedido;
import beans.RelatorioProduto;
import dados.IRepositorioCliente;
import dados.IRepositorioFornecedores;
import dados.IRepositorioPedidos;
import dados.IRepositorioProdutos;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Reúne as consultas e a exportação dos relatórios do sistema. */
public class GerenciadorRelatorios implements IGerenciadorRelatorios {
    private final IRepositorioProdutos repoProdutos;
    private final IRepositorioCliente repoClientes;
    private final IRepositorioFornecedores repoFornecedores;
    private final IRepositorioPedidos repoPedidos;

    public GerenciadorRelatorios(IRepositorioPedidos repoPedidos) {
        this(null, null, null, repoPedidos);
    }

    public GerenciadorRelatorios(IRepositorioProdutos repoProdutos,
                                 IRepositorioCliente repoClientes,
                                 IRepositorioFornecedores repoFornecedores) {
        this(repoProdutos, repoClientes, repoFornecedores, null);
    }

    public GerenciadorRelatorios(IRepositorioProdutos repoProdutos,
                                 IRepositorioCliente repoClientes,
                                 IRepositorioFornecedores repoFornecedores,
                                 IRepositorioPedidos repoPedidos) {
        this.repoProdutos = repoProdutos;
        this.repoClientes = repoClientes;
        this.repoFornecedores = repoFornecedores;
        this.repoPedidos = repoPedidos;
    }

    @Override
    public List<Produto> relatorioProdutosPorCategoria(Class<?> categoria) {
        List<Produto> filtrados = new ArrayList<>();
        if (repoProdutos == null || categoria == null) return filtrados;
        for (Produto produto : repoProdutos.listarTodos()) {
            if (categoria.isInstance(produto)) filtrados.add(produto);
        }
        return filtrados;
    }

    @Override
    public List<Fornecedor> relatorioFornecedoresPorMoeda(Moeda moeda) {
        List<Fornecedor> filtrados = new ArrayList<>();
        if (repoFornecedores == null || moeda == null) return filtrados;
        for (Fornecedor fornecedor : repoFornecedores.listarTodos()) {
            if (fornecedor.getMoedaPadrao() == moeda) filtrados.add(fornecedor);
        }
        return filtrados;
    }

    @Override
    public List<Cliente> relatorioClientesPorNome(String termo) {
        List<Cliente> filtrados = new ArrayList<>();
        if (repoClientes == null) return filtrados;
        String busca = termo == null ? "" : termo.toLowerCase(Locale.ROOT);
        for (Cliente cliente : repoClientes.listarTodos()) {
            if (cliente.getNome() != null
                    && cliente.getNome().toLowerCase(Locale.ROOT).contains(busca)) {
                filtrados.add(cliente);
            }
        }
        return filtrados;
    }

    @Override
    public double calcularValorTotalProdutos() {
        if (repoProdutos == null) return 0.0;
        double total = 0.0;
        for (Produto produto : repoProdutos.listarTodos()) total += produto.getPreco();
        return total;
    }

    /** Inclui as datas inicial e final no filtro. */
    @Override
    public List<RelatorioPedido> relatorioPedidosPorPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        List<RelatorioPedido> resultado = new ArrayList<>();
        if (repoPedidos == null) return resultado;

        for (Pedido pedido : repoPedidos.listarTodos()) {
            if (pedido == null) continue;
            LocalDate data = pedido.getDataCriacao() == null
                    ? LocalDate.now() : pedido.getDataCriacao().toLocalDate();
            if (data.isBefore(inicio) || data.isAfter(fim)) continue;

            double totalConvertido = 0.0;
            double impostoCalculado = 0.0;
            boolean conversaoCompleta = true;
            for (Item item : pedido.getItemPedido()) {
                if (!item.isConvertidoParaReal()) {
                    conversaoCompleta = false;
                    continue;
                }

                double subtotal = valor(item.getPrecoUnitario()) * quantidade(item);
                totalConvertido += subtotal;
                impostoCalculado += subtotal * valor(item.getAliquota()) / 100.0;
            }

            double impostoArrecadado = statusPagoOuDepois(pedido.getStatus())
                    ? impostoCalculado : 0.0;
            resultado.add(new RelatorioPedido(
                    pedido.getId(), data,
                    pedido.getCliente() == null ? "" : texto(pedido.getCliente().getNome()),
                    pedido.getFornecedor() == null ? "" : texto(pedido.getFornecedor().getNome()),
                    texto(pedido.getStatus()), arredondar(totalConvertido),
                    arredondar(impostoArrecadado), conversaoCompleta));
        }

        resultado.sort(Comparator.comparing(RelatorioPedido::getData)
                .thenComparing(RelatorioPedido::getIdPedido, Comparator.nullsLast(Long::compareTo)));
        return resultado;
    }

    /** Agrupa por categoria e produto os itens de pedidos pagos ou em etapa posterior. */
    @Override
    public List<RelatorioProduto> relatorioProdutosMaisImportados(
            LocalDate inicio, LocalDate fim, String categoria) {
        validarPeriodo(inicio, fim);
        Map<String, ProdutoAcumulado> acumulados = new LinkedHashMap<>();
        if (repoPedidos == null) return new ArrayList<>();
        String filtroCategoria = normalizar(categoria);
        if (filtroCategoria.equals("todas")) filtroCategoria = "";

        for (Pedido pedido : repoPedidos.listarTodos()) {
            if (pedido == null || pedido.getDataCriacao() == null) continue;
            LocalDate data = pedido.getDataCriacao().toLocalDate();
            if (data.isBefore(inicio) || data.isAfter(fim)
                    || !statusPagoOuDepois(pedido.getStatus())) continue;

            for (Item item : pedido.getItemPedido()) {
                String nomeCategoria = texto(item.getCategoria());
                if (nomeCategoria.isBlank()) nomeCategoria = "Não informada";
                if (!filtroCategoria.isBlank()
                        && !normalizar(nomeCategoria).equals(filtroCategoria)) continue;

                String nomeProduto = texto(item.getNomeProduto());
                String identificador = item.getIdProduto() == null
                        ? normalizar(nomeProduto) : item.getIdProduto().toString();
                String chave = normalizar(nomeCategoria) + "|" + identificador;
                ProdutoAcumulado acumulado = acumulados.get(chave);
                if (acumulado == null) {
                    acumulado = new ProdutoAcumulado(nomeCategoria, nomeProduto);
                    acumulados.put(chave, acumulado);
                }
                acumulado.quantidade += quantidade(item);

                if (item.isConvertidoParaReal()) {
                    acumulado.totalConvertido += valor(item.getPrecoUnitario()) * quantidade(item);
                } else {
                    acumulado.conversaoCompleta = false;
                }
            }
        }

        List<RelatorioProduto> resultado = new ArrayList<>();
        for (ProdutoAcumulado acumulado : acumulados.values()) {
            resultado.add(new RelatorioProduto(acumulado.categoria, acumulado.nome,
                    acumulado.quantidade, arredondar(acumulado.totalConvertido),
                    acumulado.conversaoCompleta));
        }
        resultado.sort(Comparator.comparingInt(RelatorioProduto::getQuantidade).reversed()
                .thenComparing(RelatorioProduto::getProduto, String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    @Override
    public void exportarPedidosCsv(List<RelatorioPedido> linhas, Path destino) throws IOException {
        StringBuilder csv = new StringBuilder("\uFEFF");
        adicionarLinha(csv, "ID do Pedido", "Data", "Cliente", "Fornecedor", "Status",
                "Total convertido (R$)", "Impostos arrecadados (R$)", "Conversão");
        for (RelatorioPedido linha : linhas) {
            adicionarLinha(csv, String.valueOf(linha.getIdPedido()), linha.getDataFormatada(),
                    linha.getCliente(), linha.getFornecedor(), linha.getStatus(),
                    decimalCsv(linha.getTotalConvertido()), decimalCsv(linha.getImpostosArrecadados()),
                    linha.getSituacaoConversao());
        }
        Files.writeString(destino, csv.toString(), StandardCharsets.UTF_8);
    }

    @Override
    public void exportarProdutosCsv(List<RelatorioProduto> linhas, Path destino) throws IOException {
        StringBuilder csv = new StringBuilder("\uFEFF");
        adicionarLinha(csv, "Categoria", "Produto", "Unidades importadas",
                "Total convertido (R$)", "Conversão");
        for (RelatorioProduto linha : linhas) {
            adicionarLinha(csv, linha.getCategoria(), linha.getProduto(),
                    String.valueOf(linha.getQuantidade()), decimalCsv(linha.getTotalConvertido()),
                    linha.getSituacaoConversao());
        }
        Files.writeString(destino, csv.toString(), StandardCharsets.UTF_8);
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Informe as datas inicial e final.");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial deve ser anterior à data final.");
        }
    }

    private boolean statusPagoOuDepois(String status) {
        String valor = normalizar(status).replace('_', ' ');
        return valor.equals("pago") || valor.equals("em transito")
                || valor.equals("transito") || valor.equals("alfandega")
                || valor.equals("entregue");
    }

    private int quantidade(Item item) {
        return item.getQuantidade() == null ? 0 : item.getQuantidade();
    }

    private double valor(Double numero) {
        return numero == null ? 0.0 : numero;
    }

    private double arredondar(double numero) {
        return Math.round(numero * 100.0) / 100.0;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private String normalizar(String valor) {
        if (valor == null) return "";
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").trim().toLowerCase(Locale.ROOT);
    }

    private String decimalCsv(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    private void adicionarLinha(StringBuilder csv, String... campos) {
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) csv.append(';');
            String campo = campos[i] == null ? "" : campos[i];
            csv.append('"').append(campo.replace("\"", "\"\"")).append('"');
        }
        csv.append(System.lineSeparator());
    }

    private static class ProdutoAcumulado {
        private final String categoria;
        private final String nome;
        private int quantidade;
        private double totalConvertido;
        private boolean conversaoCompleta = true;

        private ProdutoAcumulado(String categoria, String nome) {
            this.categoria = categoria;
            this.nome = nome;
        }
    }
}
