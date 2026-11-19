package beans;

import dados.RepositorioProdutos;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Fornecedor {
    private static final AtomicLong SEQUENCIA_ID = new AtomicLong(1);

    private final Long id;
    private String nome;
    private String paisOrigem;
    private Moeda moedaPadrao;
    private transient RepositorioProdutos listaProdutos;

    public Fornecedor() {
        this.id = SEQUENCIA_ID.getAndIncrement();
        this.listaProdutos = new RepositorioProdutos(false);
    }

    public Fornecedor(String nome, String paisOrigem, Moeda moedaPadrao) {
        this.id = SEQUENCIA_ID.getAndIncrement();
        this.nome = nome;
        this.paisOrigem = paisOrigem;
        this.moedaPadrao = moedaPadrao;
        this.listaProdutos = new RepositorioProdutos(false);
    }

    public static void sincronizarSequenciaId(long maiorIdExistente) {
        SEQUENCIA_ID.updateAndGet(atual -> Math.max(atual, maiorIdExistente + 1));
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public Moeda getMoedaPadrao() {
        return moedaPadrao;
    }

    public void setMoedaPadrao(Moeda moedaPadrao) {
        this.moedaPadrao = moedaPadrao;
    }

    public RepositorioProdutos getListaProdutos() {
        if (this.listaProdutos == null) {
            this.listaProdutos = new RepositorioProdutos(false);
        }
        return listaProdutos;
    }

    public void setListaProdutos(RepositorioProdutos listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fornecedor other = (Fornecedor) obj;

        if (this.id != null && other.id != null) {
            return Objects.equals(this.id, other.id);
        }
        return Objects.equals(
                this.nome != null ? this.nome.toLowerCase().trim() : null,
                other.nome != null ? other.nome.toLowerCase().trim() : null
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : (nome != null ? nome.toLowerCase().trim() : 0));
    }

    @Override
    public String toString() {
        return "Fornecedor [ID=" + id + ", Nome=" + nome + ", País=" + paisOrigem + ", Moeda=" + moedaPadrao + "]";
    }
}