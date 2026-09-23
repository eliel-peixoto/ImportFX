public abstract class Produto {
    private String nome;
    private int preco;
    private int aliquota;
    private Fornecedor fornecedor;

    public Produto(String nome, int preco, int aliquota, Fornecedor fornecedor) {
        this.nome = nome;
        this.preco = preco;
        this.aliquota = aliquota;
        this.fornecedor = fornecedor;
    }
}