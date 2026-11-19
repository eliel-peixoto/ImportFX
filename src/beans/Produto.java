package beans;

import java.util.Objects;

public abstract class Produto {
    private long id;
    private Double preco;
    private String nome;
    private Double precoMoedaOrigem;
    private Double peso;
    private Long idFornecedor;
    private int quantidadeEstoque;

//Construtor 
    public Produto(long id, String nome, Double precoMoedaOrigem, Double peso) {
        this.setId(id);
        this.nome = nome;
        this.precoMoedaOrigem = precoMoedaOrigem;
        this.preco = precoMoedaOrigem; 
        this.peso = peso;
        this.quantidadeEstoque = 0;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = Math.max(0, quantidadeEstoque);
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = (preco != null) ? preco : 0.0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPrecoMoedaOrigem() {
        return precoMoedaOrigem;
    }

    public void setPrecoMoedaOrigem(Double precoMoedaOrigem) {
        this.precoMoedaOrigem = precoMoedaOrigem;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public abstract Double calcularImposto();

    @Override
    public String toString() {
        return String.format("""
                [PRODUTO]
                Nome:    %s
                Id:      %d
                Preço:   %.2f
                Peso:    %.2f
                Estoque: %d
                """, this.getNome(), this.getId(), this.getPrecoMoedaOrigem(), this.getPeso(), this.quantidadeEstoque);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Produto o = (Produto) obj;

        return this.getId() == o.getId() &&
               Objects.equals(this.getNome(), o.getNome()) &&
               Objects.equals(this.getPrecoMoedaOrigem(), o.getPrecoMoedaOrigem()) &&
               Objects.equals(this.getPeso(), o.getPeso());
    }
}