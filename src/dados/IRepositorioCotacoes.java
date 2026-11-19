package dados;

import beans.Cotacao;
import beans.Moeda;

public interface IRepositorioCotacoes {
    void salvar(Cotacao cotacao);          // guarda a última cotação da moeda (substitui a anterior)
    Cotacao buscarUltima(Moeda moeda);     // null se nunca houve cotação para a moeda
}