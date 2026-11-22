package beans;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public enum StatusPedido {

    @SerializedName(value = "Aguardando Pagamento", alternate = {"Pendente", "AGUARDANDO_PAGAMENTO"})
    AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),

    @SerializedName(value = "Pago", alternate = {"PAGO"})
    PAGO("Pago"),

    @SerializedName(value = "Em Trânsito", alternate = {"EM_TRANSITO"})
    EM_TRANSITO("Em Trânsito"),

    @SerializedName(value = "Alfândega", alternate = {"ALFANDEGA"})
    ALFANDEGA("Alfândega"),

    @SerializedName(value = "Entregue", alternate = {"ENTREGUE"})
    ENTREGUE("Entregue"),

    @SerializedName(value = "Cancelado", alternate = {"CANCELADO"})
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isPosPagamento() {
        return this == PAGO || this == EM_TRANSITO || this == ALFANDEGA || this == ENTREGUE;
    }

    public List<StatusPedido> proximosStatusValidos() {
        switch (this) {
            case AGUARDANDO_PAGAMENTO:
                return List.of(PAGO);
            case PAGO:
                return List.of(EM_TRANSITO);
            case EM_TRANSITO:
                return List.of(ALFANDEGA);
            case ALFANDEGA:
                return List.of(ENTREGUE);
            case ENTREGUE:
            case CANCELADO:
            default:
                return Collections.emptyList();
        }
    }

    public boolean podeTransicionarPara(StatusPedido destino) {
        return destino != null && proximosStatusValidos().contains(destino);
    }

    public static StatusPedido fromDescricao(String valor) {
        if (valor == null || valor.isBlank() || "Pendente".equalsIgnoreCase(valor.trim())) {
            return AGUARDANDO_PAGAMENTO;
        }
        String texto = valor.trim();
        for (StatusPedido s : values()) {
            if (s.descricao.equalsIgnoreCase(texto) || s.name().equalsIgnoreCase(texto)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de pedido inválido: " + valor);
    }

    @Override
    public String toString() {
        return descricao;
    }
}