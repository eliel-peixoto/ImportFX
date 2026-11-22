package negocio;

import beans.Fornecedor;
import beans.Moeda;
import beans.Pedido;
import dados.IRepositorioFornecedores;
import dados.Repositorios;
import java.util.List;

public class GerenciadorFornecedor implements IGerenciadorFornecedor {

    private static GerenciadorFornecedor instance;
    private final IRepositorioFornecedores repositorio;

    private GerenciadorFornecedor(IRepositorioFornecedores repositorio) {
        this.repositorio = repositorio;
    }

    public static synchronized GerenciadorFornecedor getInstance() {
        if (instance == null) {
            instance = new GerenciadorFornecedor(Repositorios.fornecedores());
        }
        return instance;
    }

    @Override
    public void cadastrarFornecedor(Fornecedor fornecedor) throws Exception {
        if (fornecedor == null) {
            throw new Exception("O fornecedor não pode ser nulo.");
        }
        if (fornecedor.getNome() == null || fornecedor.getNome().trim().isEmpty()) {
            throw new Exception("O nome do fornecedor é obrigatório.");
        }
        if (fornecedor.getPaisOrigem() == null || fornecedor.getPaisOrigem().trim().isEmpty()) {
            throw new Exception("O país de origem é obrigatório.");
        }
        if (fornecedor.getMoedaPadrao() == null) {
            throw new Exception("A moeda padrão é obrigatória.");
        }

        repositorio.cadastrar(fornecedor);
    }

    @Override
    public void atualizarFornecedor(long id, String nome, String paisOrigem, Moeda moedaPadrao) throws Exception {
        Fornecedor existente = buscarFornecedor(id);
        if (existente == null) {
            throw new Exception("Fornecedor não encontrado para atualização.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new Exception("O nome do fornecedor é obrigatório.");
        }
        if (paisOrigem == null || paisOrigem.trim().isEmpty()) {
            throw new Exception("O país de origem é obrigatório.");
        }
        if (moedaPadrao == null) {
            throw new Exception("A moeda padrão é obrigatória.");
        }
        if (existente.getMoedaPadrao() != moedaPadrao) {
            boolean temProdutos = !existente.getListaProdutos().listarTodos().isEmpty();
            boolean temPedidos = Repositorios.pedidos().listarTodos().stream()
                    .anyMatch(p -> p.getFornecedor() != null
                            && p.getFornecedor().getId() != null
                            && p.getFornecedor().getId() == id);
            if (temProdutos || temPedidos) {
                throw new Exception("Não é possível trocar a moeda de um fornecedor que já possui produtos ou pedidos.");
            }
        }

        existente.setNome(nome.trim());
        existente.setPaisOrigem(paisOrigem.trim());
        existente.setMoedaPadrao(moedaPadrao);
        repositorio.atualizar(existente);
    }

    @Override
    public List<Fornecedor> listarTodos() {
        return repositorio.listarTodos();
    }

    @Override
    public Fornecedor buscarFornecedor(long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public void removerFornecedor(long id) throws Exception {
        if (buscarFornecedor(id) == null) {
            throw new Exception("Fornecedor não encontrado para remoção.");
        }
        for (Pedido p : Repositorios.pedidos().listarTodos()) {
            if (p.getFornecedor() != null && p.getFornecedor().getId() != null
                    && p.getFornecedor().getId() == id) {
                String status = p.getStatus() == null ? "" : p.getStatus().trim();
                boolean encerrado = status.equalsIgnoreCase("Entregue")
                        || status.equalsIgnoreCase("Cancelado");
                if (!encerrado) {
                    throw new Exception("Não é possível remover este fornecedor: existe pelo menos um pedido em andamento (#" + p.getId() + ").");
                }
            }
        }
        repositorio.remover(id);
    }
}