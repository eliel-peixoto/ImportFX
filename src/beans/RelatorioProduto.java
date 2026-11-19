package beans;

import java.util.Locale;

/** Uma linha agregada do relatório de produtos importados. */
public class RelatorioProduto {
    private final String categoria;
    private final String produto;
    private final int quantidade;
    private final double totalConvertido;
    private final boolean conversaoCompleta;

    public RelatorioProduto(String categoria, String produto, int quantidade,
                            double totalConvertido, boolean conversaoCompleta) {
        this.categoria = categoria;
        this.produto = produto;
        this.quantidade = quantidade;
        this.totalConvertido = totalConvertido;
        this.conversaoCompleta = conversaoCompleta;
    }

    public String getCategoria() { return categoria; }
    public String getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getTotalConvertido() { return totalConvertido; }
    public String getTotalConvertidoFormatado() {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", totalConvertido);
    }
    public boolean isConversaoCompleta() { return conversaoCompleta; }
    public String getSituacaoConversao() { return conversaoCompleta ? "Completa" : "Pendente"; }
}
