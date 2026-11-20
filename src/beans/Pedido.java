package beans;

import dados.Repositorios;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Pedido {
    private Cliente cliente;
    private Fornecedor fornecedor;
    private Long id;
    private String status;
    private ArrayList<Item> listaItems;
    private LocalDateTime dataCriacao;
    private Cotacao cotacao;

    public Pedido(Cliente cliente, Fornecedor fornecedor, Long id, String status) {
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.id = id;
        this.status = status;
        this.listaItems = new ArrayList<Item>();
        this.dataCriacao = LocalDateTime.now();
    }

    // req20

    public boolean isStatusPosPagamento() {
        if (this.status == null) {
            return false;
        }
        String s = this.status.trim();
        return s.equalsIgnoreCase("Pago")
                || s.equalsIgnoreCase("Em Trânsito")
                || s.equalsIgnoreCase("Em Transito")
                || s.equalsIgnoreCase("Alfândega")
                || s.equalsIgnoreCase("Alfandega")
                || s.equalsIgnoreCase("Entregue");
    }

    private boolean isBloqueadoParaAlteracaoItens() {
        return isStatusPosPagamento();
    }

    public void adicionar(Item item) {
        if (isBloqueadoParaAlteracaoItens()) {
            throw new IllegalStateException(
                    "Não é permitido adicionar itens a um pedido com status \"" + this.status + "\".");
        }
        this.listaItems.add(item);
    }

    public void remover(int i) {
        if (isBloqueadoParaAlteracaoItens()) {
            throw new IllegalStateException(
                    "Não é permitido remover itens de um pedido com status \"" + this.status + "\".");
        }
        this.listaItems.remove(i);
    }

    public int tamanho() {
        return this.listaItems.size();
    }

    public String getListaProduto(int i) {
        Item item = this.listaItems.get(i);
        return item.toString();
    }

    public ArrayList<Item> getItemPedido() {
        return this.listaItems;
    }

    //calculos de impostos e totais

    public boolean isPedidoNacional() {
        return fornecedor != null
                && fornecedor.getPaisOrigem() != null
                && fornecedor.getPaisOrigem().trim().equalsIgnoreCase("Brasil");
    }

    public double getPesoTotalDeclarado() {
        double pesoTotal = 0.0;
        for (Item item : listaItems) {
            if (item.getPesoTotal() != null) {
                pesoTotal += item.getPesoTotal();
            }
        }
        return pesoTotal;
    }

    public double getSubtotalItemEmReal(Item item) {
        if (item == null) return 0.0;
        int qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
        double preco = item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0;
        double subtotalOriginal = qtd * preco;

        if (item.isConvertidoParaReal()) {
            return subtotalOriginal;
        }
        if (this.cotacao != null && !this.cotacao.getMoeda().isReal()) {
            return this.cotacao.paraReal(BigDecimal.valueOf(subtotalOriginal)).doubleValue();
        }
        return subtotalOriginal;
    }

    public double getSubtotalItens() {
        double subtotal = 0.0;
        for (Item item : listaItems) {
            subtotal += getSubtotalItemEmReal(item);
        }
        return subtotal;
    }

    private BigDecimal getTaxaDolarParaReal() {
        if (this.cotacao != null && this.cotacao.getMoeda() == Moeda.USD
                && this.cotacao.getVenda() != null && this.cotacao.getVenda().signum() > 0) {
            return this.cotacao.getVenda();
        }
        if (Repositorios.cotacoes() != null) {
            Cotacao cacheUsd = Repositorios.cotacoes().buscarUltima(Moeda.USD);
            if (cacheUsd != null && cacheUsd.getVenda() != null && cacheUsd.getVenda().signum() > 0) {
                return cacheUsd.getVenda();
            }
        }
        if (Repositorios.pedidos() != null) {
            List<Pedido> todos = Repositorios.pedidos().listarTodos();
            if (todos != null) {
                for (Pedido p : todos) {
                    if (p != null && p.getCotacao() != null && p.getCotacao().getMoeda() == Moeda.USD
                            && p.getCotacao().getVenda() != null && p.getCotacao().getVenda().signum() > 0) {
                        return p.getCotacao().getVenda();
                    }
                }
            }
        }
        return BigDecimal.valueOf(5.00);
    }

    public double getLimiteIsencaoEmReal() {
        BigDecimal taxaUsd = getTaxaDolarParaReal();
        return BigDecimal.valueOf(50).multiply(taxaUsd).doubleValue();
    }

    
    public double getAcumuladoImportacaoCpfEmReal() {
        double acumulado = getSubtotalItens();
        if (this.cliente == null || Repositorios.pedidos() == null) {
            return acumulado;
        }
        Long cpfAtual = this.cliente.getCpf();
        Long idClienteAtual = this.cliente.getId();

        List<Pedido> todos = Repositorios.pedidos().listarTodos();
        if (todos != null) {
            for (Pedido outro : todos) {
                if (outro == null || outro == this) {
                    continue;
                }
                if (this.id != null && this.id.equals(outro.getId())) {
                    continue;
                }
                if (outro.isPedidoNacional() || outro.getCliente() == null) {
                    continue;
                }
                if (outro.getStatus() != null && outro.getStatus().trim().equalsIgnoreCase("Cancelado")) {
                    continue;
                }

                boolean mesmoCliente = (cpfAtual != null && cpfAtual.equals(outro.getCliente().getCpf()))
                        || (cpfAtual == null && idClienteAtual != null
                                && idClienteAtual.equals(outro.getCliente().getId()));

                if (mesmoCliente) {
                    acumulado += outro.getSubtotalItens();
                }
            }
        }
        return acumulado;
    }

    //REQ 21: Importações cuja soma acumulada por CPF não ultrapasse o equivalente a US\$ 50,00 são isentas de imposto de importação.
    
    public boolean isIsentoImpostoImportacao() {
        if (listaItems == null || listaItems.isEmpty() || isPedidoNacional()) {
            return false;
        }
        double limiteBRL = getLimiteIsencaoEmReal();
        return getAcumuladoImportacaoCpfEmReal() <= limiteBRL;
    }

    public double calcularImpostoItem(Item item) {
        if (item == null || isPedidoNacional() || isIsentoImpostoImportacao()) {
            return 0.0;
        }
        double subtotalItemBRL = getSubtotalItemEmReal(item);
        double aliq = item.getAliquota() != null ? item.getAliquota() : 0.0;
        double fator = (aliq > 1.0) ? (aliq / 100.0) : aliq;
        return subtotalItemBRL * fator;
    }

    public double getTotalImpostos() {
        if (isPedidoNacional() || isIsentoImpostoImportacao()) {
            return 0.0;
        }
        double totalImpostos = 0.0;
        for (Item item : listaItems) {
            totalImpostos += calcularImpostoItem(item);
        }
        return totalImpostos;
    }

    public double getValorFreteInternacional() {
        if (listaItems.isEmpty()) return 0.0;
        double peso = getPesoTotalDeclarado();
        if (isPedidoNacional()) {
            return 15.0 + (peso * 5.0);
        }
        return 45.0 + (peso * 25.0);
    }

    public String getDetalhamentoFrete() {
        if (listaItems == null || listaItems.isEmpty()) {
            return "R$ 0,00 (sem itens)";
        }
        double peso = getPesoTotalDeclarado();
        boolean nacional = isPedidoNacional();

        double taxaBase = nacional ? 15.0 : 45.0;
        double valorPorKg = nacional ? 5.0 : 25.0;
        String modalidade = nacional ? "Nacional" : "Internacional";

        return String.format(Locale.forLanguageTag("pt-BR"),
                "%s: Base R$ %.2f + (%.2f kg x R$ %.2f/kg)",
                modalidade, taxaBase, peso, valorPorKg);
    }

    public double getValorTotalFinal() {
        return getSubtotalItens() + getTotalImpostos() + getValorFreteInternacional();
    }

    //getters e setters

    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Fornecedor getFornecedor() {
        return this.fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void congelarCotacao(Cotacao cotacao) {
        if (this.cotacao != null) {
            throw new IllegalStateException("A cotação deste pedido já foi congelada.");
        }
        this.cotacao = Objects.requireNonNull(cotacao, "A cotação não pode ser nula.");
    }

    public Cotacao getCotacao() {
        return cotacao;
    }
}