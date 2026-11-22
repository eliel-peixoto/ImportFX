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
        if (cliente.getEndereco() == null || cliente.getEndereco().trim().isEmpty()) {
            throw new Exception("O endereço do cliente é obrigatório.");
        }
        if (cliente.getCpf() == null) {
            throw new Exception("O CPF do cliente é obrigatório.");
        }
        if (!cpfValido(cliente.getCpf())) {
            throw new Exception("CPF inválido. Verifique os 11 dígitos informados.");
        }
        for (Cliente existente : repositorio.listarTodos()) {
            if (existente.getCpf() != null && existente.getCpf().equals(cliente.getCpf())) {
                throw new Exception("Já existe um cliente cadastrado com este CPF.");
            }
        }
        cliente.gerarId();
        repositorio.cadastrar(cliente);
    }

    public static boolean cpfValido(long cpf) {
        if (cpf <= 0 || cpf > 99_999_999_999L) {
            return false;
        }
        String d = String.format("%011d", cpf);
        if (d.chars().distinct().count() == 1) {
            return false;
        }
        for (int t = 9; t <= 10; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) {
                soma += (d.charAt(i) - '0') * (t + 1 - i);
            }
            int dv = (soma * 10) % 11;
            if (dv == 10) {
                dv = 0;
            }
            if (dv != d.charAt(t) - '0') {
                return false;
            }
        }
        return true;
    }

    @Override
    public void atualizarCliente(long id, String nome, String endereco) throws Exception {
        atualizarCliente(id, nome, null, endereco);
    }

    @Override
    public void atualizarCliente(long id, String nome, Long cpf, String endereco) throws Exception {
        Cliente existente = buscarCliente(id);
        if (existente == null) {
            throw new Exception("Cliente não encontrado para atualização.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new Exception("O nome do cliente é obrigatório.");
        }
        if (cpf != null && existente.getCpf() != null && !existente.getCpf().equals(cpf)) {
            throw new Exception("O CPF do cliente não pode ser alterado após o cadastro.");
        }
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new Exception("O endereço do cliente é obrigatório.");
        }

        existente.setNome(nome.trim());
        existente.setEndereco(endereco.trim());
        repositorio.atualizar(existente);
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