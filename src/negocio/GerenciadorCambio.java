package negocio;

import beans.Cotacao;
import beans.Moeda;
import dados.IClientePtax;
import dados.IRepositorioCotacoes;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        if (moeda.isReal()) {
            // Real não precisa de conversão: cotação neutra, sem rede e sem cache
            return new Cotacao(moeda, BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now());
        }
        if (!moeda.temPtax()) {
            // Falha logo, sem requisições de rede
            throw new Exception("O PTAX do Banco Central não publica cotação de " + moeda
                    + ". Use uma das moedas suportadas: " + Moeda.suportadas() + ".");
        }

        try {
            Cotacao atual = clientePtax.consultar(moeda);
            repositorio.salvar(atual); // atualiza o cache a cada consulta bem-sucedida
            return atual;
        } catch (IOException | RuntimeException erroApi) {
            // API indisponivel OU resposta invalida: tenta o cache e sinaliza que pode estar desatualizada
            Cotacao emCache = repositorio.buscarUltima(moeda);
            if (emCache != null) {
                return emCache.comoDesatualizada();
            }
            throw new Exception("Nenhuma cotação disponível para " + moeda
                    + " (API indisponível ou resposta inválida, e sem cotação em cache).", erroApi);
        }
    }

    @Override
    public BigDecimal converterParaReal(BigDecimal valor, Cotacao cotacao) {
        if (cotacao == null) {
            throw new IllegalArgumentException("A cotação não pode ser nula.");
        }
        return cotacao.paraReal(valor); // única regra de conversão: a da própria Cotação
    }
}