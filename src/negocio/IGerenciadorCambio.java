package negocio;

import beans.Cotacao;
import beans.Moeda;
import java.math.BigDecimal;

public interface IGerenciadorCambio {
    Cotacao obterCotacao(Moeda moeda) throws Exception;

    /** Converte para Real (cotação de venda, 2 casas, HALF_UP). */
    BigDecimal converterParaReal(BigDecimal valor, Cotacao cotacao);

    default double converterParaReal(double valor, Cotacao cotacao) {
        return converterParaReal(BigDecimal.valueOf(valor), cotacao).doubleValue();
    }
}