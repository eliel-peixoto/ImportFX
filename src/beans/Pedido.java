package beans;

import java.util.ArrayList;
import java.util.Objects;
import java.time.LocalDateTime;

public class Pedido {
    private Cliente cliente;
    private Fornecedor fornecedor;
    private Long id; 
    private String status;
    private ArrayList<Item> listaItems;
    private LocalDateTime dataCriacao;
    private Cotacao cotacao;

    public Pedido(Cliente cliente, Fornecedor fornecedor, Long id, String status) {
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.id = id;
        this.status = status;
        this.listaItems = new ArrayList<Item>();
        this.dataCriacao = LocalDateTime.now();
    }
    
    public void adicionar(Item item) {
        this.listaItems.add(item);
    }
    
    public void remover(int i) {
        this.listaItems.remove(i);
    }
    
    public int tamanho() {
        return this.listaItems.size();
    }
    
    public String getListaProduto(int i) {
        Item item = this.listaItems.get(i);
        return item.toString();
    }
    
    public ArrayList<Item> getItemPedido() {
        return this.listaItems;
    }
    
    //Getters e Setters
    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Fornecedor getFornecedor() {
        return this.fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    /** Congela no pedido a cotação usada na conversão dos itens. Só pode ser feito uma vez. */
    public void congelarCotacao(Cotacao cotacao) {
        if (this.cotacao != null) {
            throw new IllegalStateException("A cotação deste pedido já foi congelada.");
        }
        this.cotacao = Objects.requireNonNull(cotacao, "A cotação não pode ser nula.");
    }

    /** Cotação usada no pedido; null se o pedido está em Real (sem conversão). */
    public Cotacao getCotacao() {
        return cotacao;
    }
}
