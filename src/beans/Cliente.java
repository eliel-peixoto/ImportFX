package beans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Cliente {
    private static final AtomicLong SEQUENCIA_ID = new AtomicLong(1);

    private Long id;
    private String nome;
    private Long cpf;
    private String endereco;
    private ArrayList<Pedido> pedido;
    private ArrayList<Notificacao> notificacoes;

    public Cliente() {
        this.pedido = new ArrayList<>();
        this.notificacoes = new ArrayList<>();
    }

    public Cliente(String nome, Long cpf, String endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.pedido = new ArrayList<>();
        this.notificacoes = new ArrayList<>();
    }

    public void gerarId() {
        if (this.id == null) {
            this.id = SEQUENCIA_ID.getAndIncrement();
        }
    }

    public static void sincronizarSequenciaId(long maiorIdExistente) {
        SEQUENCIA_ID.updateAndGet(atual -> Math.max(atual, maiorIdExistente + 1));
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getCpf() { return cpf; }
    public void setCpf(Long cpf) {
        if (this.cpf != null && !this.cpf.equals(cpf)) {
            throw new IllegalStateException("O CPF não pode ser alterado após ser cadastrado.");
        }
        if (this.cpf == null) {
            this.cpf = cpf;
        }
    }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public ArrayList<Pedido> getPedido() { return pedido; }
    public void setPedido(ArrayList<Pedido> pedido) { this.pedido = pedido; }

    //notificações

    public List<Notificacao> getNotificacoes() {
        if (this.notificacoes == null) {
            this.notificacoes = new ArrayList<>();
        }
        return this.notificacoes;
    }

    public void adicionarNotificacao(String mensagem, Long idPedido) {
        // Insere no topo (index 0) para que as mais recentes apareçam primeiro
        getNotificacoes().add(0, new Notificacao(idPedido, mensagem));
    }

    public long getQuantidadeNotificacoesNaoLidas() {
        return getNotificacoes().stream().filter(n -> !n.isLida()).count();
    }

    public void marcarTodasNotificacoesComoLidas() {
        for (Notificacao n : getNotificacoes()) {
            n.setLida(true);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente other = (Cliente) obj;
        return this.cpf != null && this.cpf.equals(other.cpf);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", endereco='" + endereco + '\'' +
                '}';
    }
}