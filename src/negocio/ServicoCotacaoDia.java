package negocio;

import beans.Moeda;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServicoCotacaoDia {

    private final IGerenciadorCambio cambio;

    public ServicoCotacaoDia(IGerenciadorCambio cambio) {
        this.cambio = cambio;
    }

    /** Moedas com cotação PTAX. */
    public static List<Moeda> moedasExibidas() {
        return Moeda.suportadas().stream().filter(m -> !m.isReal()).toList();
    }

    public List<CotacaoDoDia> consultarTodas(Consumer<String> progresso) {
        List<Moeda> moedas = moedasExibidas();
        List<CotacaoDoDia> resultado = new ArrayList<>();
        int posicao = 0;
        for (Moeda moeda : moedas) {
            posicao++;
            progresso.accept("Consultando " + moeda + " (" + posicao + "/" + moedas.size() + ")...");
            try {
                resultado.add(new CotacaoDoDia(moeda, cambio.obterCotacao(moeda), null));
            } catch (Exception e) {
                String motivo = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                resultado.add(new CotacaoDoDia(moeda, null, motivo));
            }
        }
        return resultado;
    }

    /** Texto de aviso para a tela; vazio quando todas as cotações estão normais. */
    public static String resumoAviso(List<CotacaoDoDia> lista) {
        int semCotacao = 0;
        int doCache = 0;
        StringBuilder moedasSem = new StringBuilder();
        for (CotacaoDoDia c : lista) {
            if (!c.isDisponivel()) {
                semCotacao++;
                if (moedasSem.length() > 0) moedasSem.append(", ");
                moedasSem.append(c.getMoeda());
            } else if (c.isDesatualizada()) {
                doCache++;
            }
        }
        if (!lista.isEmpty() && semCotacao == lista.size()) {
            return "Não foi possível obter nenhuma cotação. Verifique a conexão com a internet e clique em \"Atualizar cotações\".";
        }
        StringBuilder aviso = new StringBuilder();
        if (doCache > 0) {
            aviso.append(doCache).append(" cotação(ões) vieram do cache porque a API do Banco Central está indisponível; ")
                 .append("os valores podem estar desatualizados. ");
        }
        if (semCotacao > 0) {
            aviso.append("Sem cotação disponível: ").append(moedasSem).append(".");
        }
        return aviso.toString().trim();
    }
}