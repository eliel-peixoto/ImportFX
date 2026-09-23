//import java.util.ArrayList;

public class Fornecedor {
    //atributos
    String nome;
    String PdOrigem;  //Pais de Origem
    String moedaPadr; //moeda do Pais de Origem (vamos usar a API)
    //<ArrayList> produto;

    //construtor
    public Fornecedor(String nome, String PdOrigem, String moedaPadr) {
        this.nome = nome;
        this.PdOrigem = PdOrigem;
        this.moedaPadr = moedaPadr;
    }

    //getters e setters
    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPdOrigem() {
        return this.PdOrigem;
    }

    public void setPdOrigem(String PdOrigem) {
        this.PdOrigem = PdOrigem;
    }

    public String getMoedaPadr() {
        return this.moedaPadr;
    }

    public void setMoedaPadr(String moedaPadr) {
        this.moedaPadr = moedaPadr;
    }

}
