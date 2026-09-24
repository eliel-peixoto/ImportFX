package beans;
import java.util.ArrayList;
import java.util.Objects;

public class Fornecedor {
    //atributos
    private String nome;
    private String pdOrigem;  //Pais de Origem
    private String moedaPadr; //moeda do Pais de Origem (vamos usar a API)
    private ArrayList<Produto> listaProdutos;

    //construtores=====
    //incompleto
    public Fornecedor(){
        this.listaProdutos = new ArrayList<>();
    }

    //completo mas sem lista
    public Fornecedor(String nome, String pdOrigem, String moedaPadr) {
        this.nome = nome;
        this.pdOrigem = pdOrigem;
        this.moedaPadr = moedaPadr;
        this.listaProdutos = new ArrayList<>();
    }

    //completo e com lista
    public Fornecedor(String nome, String pdOrigem, String moedaPadr, ArrayList<Produto> listaProduto) {
        this.nome = nome;
        this.pdOrigem = pdOrigem;
        this.moedaPadr = moedaPadr;
        if (listaProdutos != null) {
            this.listaProdutos = listaProdutos;
        } else {
            this.listaProdutos = new ArrayList<>();
        }
    }
    //===================
    
    //manipulação da lista
    public void adicionar(Produto produto) {
    	this.listaProdutos.add(produto);
    }
    public void remover(int i) {
    	this.listaProdutos.remove(i);
    }
    public int tamanho() {
    	int i = this.listaProdutos.size();
    	return i;
    }
    public String getListaProduto(int i) {
    	Produto p = this.listaProdutos.get(i);
    	return p.toString();
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
    public ArrayList<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setProduto(ArrayList<Produto> produto) {
        this.listaProdutos = produto;
    }

    //adicionar e remover produtos
    public void adicionarProduto(Produto p) {
        if (p != null && !this.listaProdutos.contains(p)) {
            this.listaProdutos.add(p);
        }
    }

    public void removerProduto(Produto p) {
        this.listaProdutos.remove(p);
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
                ", qtdProdutos=" + (listaProdutos != null ? listaProdutos.size() : 0) +
                '}';
    }

}
