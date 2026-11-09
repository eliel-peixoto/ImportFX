package negocio;

import beans.Cliente;
import dados.IRepositorioCliente; 
import java.util.List;

public class GerenciadorCliente implements IGerenciadorCliente {
    
    private IRepositorioCliente repositorio;

    public GerenciadorCliente(IRepositorioCliente repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void cadastrarCliente(Cliente cliente) throws Exception {
        if (cliente == null) {
            throw new Exception("O cliente não pode ser nulo.");
        }
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new Exception("O nome do cliente é obrigatório.");
        }
        if (cliente.getCpf() == null) {
            throw new Exception("O CPF do cliente é obrigatório.");
        }
        String cpfStr = String.valueOf(cliente.getCpf());
        if (cpfStr.length() != 11) {
            throw new Exception("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
        repositorio.cadastrar(cliente);
    }

    @Override
    public List<Cliente> listarTodos() {
        return repositorio.listarTodos();
    }

    @Override
    public Cliente buscarCliente(long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public void removerCliente(long id) throws Exception {
        if (buscarCliente(id) == null) {
            throw new Exception("Cliente não encontrado para remoção.");
        }
        repositorio.remover(id);
    }
}