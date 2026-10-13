package beans;

import java.util.Objects;
import dados.RepositorioProdutos;

public class Fornecedor {
    private Long id;
    private String nome;
    private String paisOrigem;
    private Moeda moedaPadrao;
    private RepositorioProdutos listaProdutos;

    public Fornecedor() {
        this.listaProdutos = new RepositorioProdutos();
    }

    public Fornecedor(Long id, String nome, String paisOrigem, Moeda moedaPadrao) {
        this.id = id;
        this.nome = nome;
        this.paisOrigem = paisOrigem;
        this.moedaPadrao = moedaPadrao;
        this.listaProdutos = new RepositorioProdutos();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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