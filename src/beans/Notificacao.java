package beans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notificacao {
    private Long idPedido;
    private String mensagem;
    private LocalDateTime dataHora;
    private boolean lida;
    private boolean popupExibido;

    public Notificacao(Long idPedido, String mensagem) {
        this.idPedido = idPedido;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
        this.lida = false;
        this.popupExibido = false;
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public boolean isPopupExibido() { return popupExibido; }
    public void setPopupExibido(boolean popupExibido) { this.popupExibido = popupExibido; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        String hora = (dataHora != null) ? dataHora.format(fmt) : "";
        String prefixo = lida ? "[Lida] " : "[NOVA] ";
        return prefixo + "(" + hora + ") " + mensagem;
    }
}