package beans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Uma linha do relatório de pedidos. */
public class RelatorioPedido {
    private final Long idPedido;
    private final LocalDate data;
    private final String cliente;
    private final String fornecedor;
    private final String status;
    private final double totalConvertido;
    private final double impostosArrecadados;
    private final boolean conversaoCompleta;

    public RelatorioPedido(Long idPedido, LocalDate data, String cliente, String fornecedor,
                           String status, double totalConvertido, double impostosArrecadados,
                           boolean conversaoCompleta) {
        this.idPedido = idPedido;
        this.data = data;
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.status = status;
        this.totalConvertido = totalConvertido;
        this.impostosArrecadados = impostosArrecadados;
        this.conversaoCompleta = conversaoCompleta;
    }

    public Long getIdPedido() { return idPedido; }
    public LocalDate getData() { return data; }
    public String getDataFormatada() { return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    public String getCliente() { return cliente; }
    public String getFornecedor() { return fornecedor; }
    public String getStatus() { return status; }
    public double getTotalConvertido() { return totalConvertido; }
    public String getTotalConvertidoFormatado() { return moeda(totalConvertido); }
    public double getImpostosArrecadados() { return impostosArrecadados; }
    public String getImpostosFormatados() { return moeda(impostosArrecadados); }
    public boolean isConversaoCompleta() { return conversaoCompleta; }
    public String getSituacaoConversao() { return conversaoCompleta ? "Completa" : "Pendente"; }

    private String moeda(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", valor);
    }
}
