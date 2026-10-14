package negocio;

import beans.Cliente;
import beans.Fornecedor;
import java.util.List;

public interface IGerenciadorCadastro {

    // Cliente:
    void cadastrarCliente(Cliente c);
    Cliente buscarClientePorCpf(Long cpf);
    List<Cliente> listarClientes();
    void atualizarCliente(Cliente c);
    void removerCliente(Long cpf);

    // Fornecedor:
    void cadastrarFornecedor(Fornecedor f);
    Fornecedor procurarFornecedor(int id, String nome);
    void removerFornecedor(int id, String nome);
    List<Fornecedor> listarFornecedores();
}