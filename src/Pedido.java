import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private Cliente cliente;
    private Fornecedor fornecedor;
    private int codigo;
    private String status;
    private ArrayList<Item> listaItems;

    public Pedido(Cliente cliente, Fornecedor fornecedor, int codigo, String status) {
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.codigo = codigo;
        this.status = status;
        this.listaItems = new ArrayList<Item>();
    }

    //manipulação da lista
    public void adicionar(Item item) {
    	this.listaItems.add(item);
    }
    public void remover(int i) {
    	this.listaItems.remove(i);
    }
    public int tamanho() {
    	int i = this.listaItems.size();
    	return i;
    }
    public String getListaProduto(int i) {
    	Item item = this.listaItems.get(i);
    	return item.toString();
    }
    
    
    //getters e setters
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

    public int getCodigo() {
        return this.codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
