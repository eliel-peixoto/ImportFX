package beans;
import java.util.ArrayList;

public class Cliente {
    private Long id;
    private String nome;
    private Long cpf;
    private String endereco;
    private ArrayList<Pedido> pedido;

    public Cliente(Long id, String nome, Long cpf, String endereco) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.pedido = new ArrayList<>(); 
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getCpf() { return cpf; }
    public void setCpf(Long cpf) { this.cpf = cpf; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public ArrayList<Pedido> getPedido() { return pedido; }
    public void setPedido(ArrayList<Pedido> pedido) { this.pedido = pedido; }

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