public class Pedido {
    Cliente cliente;
    Fornecedor fornecedor;
    int codigo;
    String status;

    public Pedido(Cliente cliente, Fornecedor fornecedor, int codigo, String status) {
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.codigo = codigo;
        this.status = status;
    }
}
