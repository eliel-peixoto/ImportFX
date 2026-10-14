package negocio;

import beans.Cliente;
import beans.Fornecedor;
import dados.IRepositorioCliente;
import dados.IRepositorioFornecedores;
import java.util.List;

public class GerenciadorCadastro implements IGerenciadorCadastro {
    private IRepositorioCliente repoC;
    private IRepositorioFornecedores repoF;

    public GerenciadorCadastro(IRepositorioCliente repoC, IRepositorioFornecedores repoF) {
        this.repoC = repoC;
        this.repoF = repoF;
    }

    // Cliente:

    @Override
    public void cadastrarCliente(Cliente c) {
        if (c != null) {
            this.repoC.cadastrar(c);
        }
    }

    @Override
    public Cliente buscarClientePorCpf(Long cpf) {
        return this.repoC.buscarPorCpf(cpf);
    }

    @Override
    public List<Cliente> listarClientes() {
        return this.repoC.listarTodos();
    }

    @Override
    public void atualizarCliente(Cliente c) {
        if (c != null) {
            this.repoC.atualizar(c);
        }
    }

    @Override
    public void removerCliente(Long cpf) {
        this.repoC.remover(cpf);
    }

    // Fornecedor:

    @Override
    public void cadastrarFornecedor(Fornecedor f) {
        if (f != null) {
            this.repoF.adicionarFornecedor(f);
        }
    }

    @Override
    public Fornecedor procurarFornecedor(int id, String nome) {
        return this.repoF.procurarFornecedor(id, nome);
    }

    @Override
    public void removerFornecedor(int id, String nome) {
        this.repoF.removerFornecedor(id, nome);
    }

    @Override
    public List<Fornecedor> listarFornecedores() {
        return this.repoF.listarTodos();
    }
}