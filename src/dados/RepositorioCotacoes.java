package dados;

import beans.Cotacao;
import beans.Moeda;
import java.util.EnumMap;
import java.util.Map;

/**
 * Cache local (em memória) da última cotação obtida por moeda.
 */

public class RepositorioCotacoes implements IRepositorioCotacoes {

    private final Map<Moeda, Cotacao> cache;

    public RepositorioCotacoes() {
        this.cache = new EnumMap<>(Moeda.class);
    }

    @Override
    public void salvar(Cotacao cotacao) {
        if (cotacao != null) {
            cache.put(cotacao.getMoeda(), cotacao);
        }
    }

    @Override
    public Cotacao buscarUltima(Moeda moeda) {
        return cache.get(moeda);
    }
}