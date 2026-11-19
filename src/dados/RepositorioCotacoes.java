package dados;

import beans.Cotacao;
import beans.Moeda;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

public class RepositorioCotacoes implements IRepositorioCotacoes {

    private static final String ARQUIVO = "cotacoes.json";
    private final Map<Moeda, Cotacao> cache;

    public RepositorioCotacoes() {
        this.cache = new EnumMap<>(Moeda.class);
        Type tipoMapa = new TypeToken<Map<Moeda, Cotacao>>() {}.getType();
        Map<Moeda, Cotacao> salvas = PersistenciaJson.carregarObjeto(ARQUIVO, tipoMapa);
        if (salvas != null) {
            this.cache.putAll(salvas);
        }
    }

    @Override
    public void salvar(Cotacao cotacao) {
        if (cotacao != null) {
            cache.put(cotacao.getMoeda(), cotacao);
            PersistenciaJson.salvarObjeto(ARQUIVO, cache);
        }
    }

    @Override
    public Cotacao buscarUltima(Moeda moeda) {
        return cache.get(moeda);
    }
}