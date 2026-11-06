package beans;

import java.time.LocalDateTime;

/**
 * Cotação PTAX de uma moeda estrangeira em relação ao Real.
 * Objeto imutável: por isso pode ser guardado no Pedido para "congelar"
 * a cotação usada na confirmação (REQ18) sem risco de ser alterado depois.
 */

public class Cotacao {
    private final Moeda moeda;
    private final double compra;
    private final double venda;
    private final LocalDateTime dataHora;
    private final boolean desatualizada; // true quando veio do cache por a API estar indisponível

    public Cotacao(Moeda moeda, double compra, double venda, LocalDateTime dataHora) {
        this(moeda, compra, venda, dataHora, false);
    }

    public Cotacao(Moeda moeda, double compra, double venda, LocalDateTime dataHora, boolean desatualizada) {
        this.moeda = moeda;
        this.compra = compra;
        this.venda = venda;
        this.dataHora = dataHora;
        this.desatualizada = desatualizada;
    }

    /** Converte um valor da moeda estrangeira para Real, usando a cotação de venda. */
    public double paraReal(double valor) {
        return valor * venda;
    }

    /** Devolve uma cópia desta cotação marcada como "do cache" (API indisponível). */
    public Cotacao comoDesatualizada() {
        return new Cotacao(moeda, compra, venda, dataHora, true);
    }

    public Moeda getMoeda() {
        return moeda;
    }

    public double getCompra() {
        return compra;
    }

    public double getVenda() {
        return venda;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public boolean isDesatualizada() {
        return desatualizada;
    }

    @Override
    public String toString() {
        return String.format("[COTAÇÃO %s] compra: R$ %.4f | venda: R$ %.4f | data: %s%s",
                moeda, compra, venda, dataHora,
                desatualizada ? " (CACHE - API indisponível)" : "");
    }
}