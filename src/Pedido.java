public class Pedido {
    Cliente cliente;
    Fornecedor fornecedor;
    int codigo;

    public Pedido(Cliente cliente, Fornecedor fornecedor, int codigo) {
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.codigo = codigo;
    }
}
