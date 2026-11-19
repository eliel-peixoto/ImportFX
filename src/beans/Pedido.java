package beans;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void adicionar(Item item) {
        this.listaItems.add(item);
    }

    public void remover(int i) {
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

    //calculos financeiros (subtotal, impostos, frete, total final)

    //Soma o peso total declarado (em kg) de todos os itens do pedido. */
    public double getPesoTotalDeclarado() {
        double pesoTotal = 0.0;
        for (Item item : listaItems) {
            if (item.getPesoTotal() != null) {
                pesoTotal += item.getPesoTotal();
            }
        }
        return pesoTotal;
    }

    // Soma o subtotal dos produtos (quantidade * preço unitário).
    public double getSubtotalItens() {
        double subtotal = 0.0;
        for (Item item : listaItems) {
            int qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
            double preco = item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0;
            subtotal += qtd * preco;
        }
        return subtotal;
    }

    //Calcula o imposto individual de um item considerando sua alíquota. 
    public double calcularImpostoItem(Item item) {
        if (item == null) return 0.0;
        int qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
        double preco = item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0;
        double subtotalItem = qtd * preco;
        double aliq = item.getAliquota() != null ? item.getAliquota() : 0.0;
        //Normaliza caso a alíquota esteja em percentual (ex: 60.0 -> 0.60) ou decimal (0.60)
        double fator = (aliq > 1.0) ? (aliq / 100.0) : aliq;
        return subtotalItem * fator;
    }

    //Soma os impostos de todos os itens do pedido.
    public double getTotalImpostos() {
        double totalImpostos = 0.0;
        for (Item item : listaItems) {
            totalImpostos += calcularImpostoItem(item);
        }
        return totalImpostos;
    }

    /**
     * Calcula o frete com base no peso total declarado:
     * - Internacional (fora do Brasil): taxa base R$ 45,00 + R$ 25,00 por kg declarado.
     * - Nacional (Brasil): taxa base R$ 15,00 + R$ 5,00 por kg declarado.
     */
    public double getValorFreteInternacional() {
        if (listaItems.isEmpty()) return 0.0;
        double peso = getPesoTotalDeclarado();
        boolean isNacional = fornecedor != null
                && fornecedor.getPaisOrigem() != null
                && fornecedor.getPaisOrigem().trim().equalsIgnoreCase("Brasil");

        if (isNacional) {
            return 15.0 + (peso * 5.0);
        }
        return 45.0 + (peso * 25.0);
    }

    public String getDetalhamentoFrete() {
        if (listaItems == null || listaItems.isEmpty()) {
            return "R$ 0,00 (sem itens)";
        }
        double peso = getPesoTotalDeclarado();
        boolean isNacional = fornecedor != null
                && fornecedor.getPaisOrigem() != null
                && fornecedor.getPaisOrigem().trim().equalsIgnoreCase("Brasil");

        double taxaBase = isNacional ? 15.0 : 45.0;
        double valorPorKg = isNacional ? 5.0 : 25.0;
        String modalidade = isNacional ? "Nacional" : "Internacional";

        return String.format(java.util.Locale.forLanguageTag("pt-BR"),
                "%s: Base R$ %.2f + (%.2f kg × R$ %.2f/kg)",
                modalidade, taxaBase, peso, valorPorKg);
    }

    public double getValorTotalFinal() {
        return getSubtotalItens() + getTotalImpostos() + getValorFreteInternacional();
    }

    // --- GETTERS E SETTERS ---

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