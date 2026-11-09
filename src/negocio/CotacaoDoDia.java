package negocio;

import beans.Cotacao;
import beans.Moeda;

/** Resultado da consulta da cotação de uma moeda: a cotação, ou o motivo de não haver. */
public class CotacaoDoDia {

    private final Moeda moeda;
    private final Cotacao cotacao;
    private final String erro;

    public CotacaoDoDia(Moeda moeda, Cotacao cotacao, String erro) {
        this.moeda = moeda;
        this.cotacao = cotacao;
        this.erro = erro;
    }

    public Moeda getMoeda() { return moeda; }
    public Cotacao getCotacao() { return cotacao; }
    public String getErro() { return erro; }

    public boolean isDisponivel() {
        return cotacao != null;
    }

    /** true se veio do cache porque a API do Banco Central estava indisponível. */
    public boolean isDesatualizada() {
        return cotacao != null && cotacao.isDesatualizada();
    }
}