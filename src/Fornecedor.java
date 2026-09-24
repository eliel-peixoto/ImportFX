import java.util.ArrayList;
import java.util.Objects;

public class Fornecedor {
    //atributos
    private String nome;
    private String pdOrigem;  //Pais de Origem
    private String moedaPadr; //moeda do Pais de Origem (vamos usar a API)
    private ArrayList<Produto> produto;

    //construtor:

    //incompleto
    public Fornecedor(){
        this.produto = new ArrayList<>();
    }

    //completo mas sem lista
    public Fornecedor(String nome, String pdOrigem, String moedaPadr) {
        this.nome = nome;
        this.pdOrigem = pdOrigem;
        this.moedaPadr = moedaPadr;
        this.produto = new ArrayList<>();
    }

    //completo e com lista
    public Fornecedor(String nome, String pdOrigem, String moedaPadr, ArrayList<Produto> produto) {
        this.nome = nome;
        this.pdOrigem = pdOrigem;
        this.moedaPadr = moedaPadr;
        if (produto != null) {
            this.produto = produto;
        } else {
            this.produto = new ArrayList<>();
        }
    }
    
    //getters e setters
    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getpdOrigem() {
        return this.pdOrigem;
    }

    public void setpdOrigem(String pdOrigem) {
        this.pdOrigem = pdOrigem;
    }

    public String getMoedaPadr() {
        return this.moedaPadr;
    }

    public void setMoedaPadr(String moedaPadr) {
        this.moedaPadr = moedaPadr;
    }
    public ArrayList<Produto> getProduto() {
        return produto;
    }

    public void setProduto(ArrayList<Produto> produto) {
        this.produto = produto;
    }

    //adicionar e remover produtos
    public void adicionarProduto(Produto p) {
        if (p != null && !this.produto.contains(p)) {
            this.produto.add(p);
        }
    }

    public void removerProduto(Produto p) {
        this.produto.remove(p);
    }

    //Sobrecrevendo a classe object
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fornecedor que = (Fornecedor) o;
        return Objects.equals(nome, que.nome) &&
               Objects.equals(pdOrigem, que.pdOrigem) &&
               Objects.equals(moedaPadr, que.moedaPadr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, pdOrigem, moedaPadr);
    }

    @Override
    public String toString() {
        return "Fornecedor{" +
                "nome='" + nome + '\'' +
                ", pdOrigem='" + pdOrigem + '\'' +
                ", moedaPadr='" + moedaPadr + '\'' +
                ", qtdProdutos=" + (produto != null ? produto.size() : 0) +
                '}';
    }

}
