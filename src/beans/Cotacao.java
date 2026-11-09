package beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

public class Cotacao {
    private final Moeda moeda;
    private final BigDecimal compra;
    private final BigDecimal venda;
    private final LocalDateTime dataHora;
    private final boolean desatualizada;

    public Cotacao(Moeda moeda, BigDecimal compra, BigDecimal venda, LocalDateTime dataHora) {
        this(moeda, compra, venda, dataHora, false);
    }

    public Cotacao(Moeda moeda, BigDecimal compra, BigDecimal venda, LocalDateTime dataHora, boolean desatualizada) {
        this.moeda = Objects.requireNonNull(moeda, "A moeda não pode ser nula.");
        this.compra = Objects.requireNonNull(compra, "A cotação de compra não pode ser nula.");
        this.venda = Objects.requireNonNull(venda, "A cotação de venda não pode ser nula.");
        this.dataHora = Objects.requireNonNull(dataHora, "A data/hora não pode ser nula.");
        if (compra.signum() <= 0 || venda.signum() <= 0) {
            throw new IllegalArgumentException("As cotações de compra e venda devem ser maiores que zero.");
        }
        this.desatualizada = desatualizada;
    }

    /** Converte para Real pela cotacao de venda. Arredonda para 2 casas com HALF_UP. */
    public BigDecimal paraReal(BigDecimal valor) {
        Objects.requireNonNull(valor, "O valor a converter não pode ser nulo.");
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("O valor a converter não pode ser negativo.");
        }
        return valor.multiply(venda).setScale(2, RoundingMode.HALF_UP);
    }

    /** copia desta cotacao marcada como "do cache" (API indisponível). */
    public Cotacao comoDesatualizada() {
        return new Cotacao(moeda, compra, venda, dataHora, true);
    }

    public Moeda getMoeda() { return moeda; }
    public BigDecimal getCompra() { return compra; }
    public BigDecimal getVenda() { return venda; }
    public LocalDateTime getDataHora() { return dataHora; }
    public boolean isDesatualizada() { return desatualizada; }

    @Override
    public String toString() {
        return String.format("[COTAÇÃO %s] compra: R$ %.4f | venda: R$ %.4f | data: %s%s",
                moeda, compra, venda, dataHora,
                desatualizada ? " (CACHE - API indisponível)" : "");
    }
}