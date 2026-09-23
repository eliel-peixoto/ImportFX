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


    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPreco() {
        return this.preco;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }

    public int getAliquota() {
        return this.aliquota;
    }

    public void setAliquota(int aliquota) {
        this.aliquota = aliquota;
    }

    public Fornecedor getFornecedor() {
        return this.fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

}