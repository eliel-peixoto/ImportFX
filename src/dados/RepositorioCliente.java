package dados;

import beans.Cliente;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCliente implements IRepositorioCliente {
    
    private List<Cliente> clientes;

    public RepositorioCliente() {
        this.clientes = new ArrayList<>();
    }

    @Override
    public void cadastrar(Cliente cliente) {
        this.clientes.add(cliente);
    }

    @Override
    public Cliente buscarPorCpf(String cpf) {
        for (Cliente c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return c;
            }
        }
        return null; 
    }

    @Override
    public List<Cliente> listarTodos() {
        return this.clientes;
    }

    @Override
    public void atualizar(Cliente clienteAtualizado) {
        Cliente antigo = buscarPorCpf(clienteAtualizado.getCpf());
        
        if (antigo != null) {
            antigo.setNome(clienteAtualizado.getNome());
            antigo.setEndereco(clienteAtualizado.getEndereco());
            antigo.setPedido(clienteAtualizado.getPedido());
        }
    }


    @Override
    public void remover(String cpf) {
        Cliente clienteParaRemover = buscarPorCpf(cpf);
        
        if (clienteParaRemover != null) {
            this.clientes.remove(clienteParaRemover);
        }
    }
}
