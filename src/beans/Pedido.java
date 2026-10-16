package beans;

import java.util.ArrayList;

public class Pedido {
    private Cliente cliente;
    private Fornecedor fornecedor;
    private Long id; 
    private String status;
    private ArrayList<Item> listaItems;

    public Pedido(Cliente cliente, Fornecedor fornecedor, Long id, String status) {
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.id = id;
        this.status = status;
        this.listaItems = new ArrayList<Item>();
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
}