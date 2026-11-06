package negocio;

import beans.Cotacao;
import beans.Moeda;

public interface IGerenciadorCambio {
    Cotacao obterCotacao(Moeda moeda) throws Exception;

    /** Converte um valor da moeda da cotação para Real, usando a cotação de venda. */
    double converterParaReal(double valor, Cotacao cotacao);
}