package negocio;

import beans.Cotacao;
import beans.Moeda;
import dados.IClientePtax;
import dados.IRepositorioCotacoes;
import java.io.IOException;

public class GerenciadorCambio implements IGerenciadorCambio {

    private IClientePtax clientePtax;
    private IRepositorioCotacoes repositorio;

    public GerenciadorCambio(IClientePtax clientePtax, IRepositorioCotacoes repositorio) {
        this.clientePtax = clientePtax;
        this.repositorio = repositorio;
    }

    @Override
    public Cotacao obterCotacao(Moeda moeda) throws Exception {
        if (moeda == null) {
            throw new Exception("A moeda não pode ser nula.");
        }

        try {
            Cotacao atual = clientePtax.consultar(moeda);
            repositorio.salvar(atual); // atualiza o cache a cada consulta bem-sucedida
            return atual;
        } catch (IOException erroApi) {
            // API indisponível: tenta o cache e sinaliza que a cotação pode estar desatualizada
            Cotacao emCache = repositorio.buscarUltima(moeda);
            if (emCache != null) {
                return emCache.comoDesatualizada();
            }
            throw new Exception("Nenhuma cotação disponível para " + moeda
                    + " (API indisponível e sem cotação em cache).");
        }
    }

    @Override
    public double converterParaReal(double valor, Cotacao cotacao) {
        if (cotacao == null) {
            throw new IllegalArgumentException("A cotação não pode ser nula.");
        }
        return valor * cotacao.getVenda();
    }
}