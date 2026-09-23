public class Pedido {
    private Cliente cliente;
    private Fornecedor fornecedor;
    private int codigo;
    private String status;

    public Pedido(Cliente cliente, Fornecedor fornecedor, int codigo, String status) {
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.codigo = codigo;
        this.status = status;
    }


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
