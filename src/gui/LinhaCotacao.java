package gui;

import beans.Cotacao;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import negocio.CotacaoDoDia;

/** Linha da tabela de cotações. */
public class LinhaCotacao {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String moeda;
    private final String compra;
    private final String venda;
    private final String data;
    private final String situacao;

    private LinhaCotacao(String moeda, String compra, String venda, String data, String situacao) {
        this.moeda = moeda;
        this.compra = compra;
        this.venda = venda;
        this.data = data;
        this.situacao = situacao;
    }

    public static LinhaCotacao de(CotacaoDoDia dia) {
        if (!dia.isDisponivel()) {
            return new LinhaCotacao(dia.getMoeda().name(), "—", "—", "—", "Indisponível");
        }
        Cotacao c = dia.getCotacao();
        return new LinhaCotacao(
                dia.getMoeda().name(),
                "R$ " + String.format(PT_BR, "%.4f", c.getCompra()),
                "R$ " + String.format(PT_BR, "%.4f", c.getVenda()),
                c.getDataHora().format(DATA_HORA),
                c.isDesatualizada() ? "Cache (pode estar desatualizada)" : "Atualizada");
    }

    public String getMoeda() { return moeda; }
    public String getCompra() { return compra; }
    public String getVenda() { return venda; }
    public String getData() { return data; }
    public String getSituacao() { return situacao; }
}