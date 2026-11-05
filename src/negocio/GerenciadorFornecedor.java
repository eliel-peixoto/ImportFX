package negocio;

import beans.Fornecedor;
import dados.IRepositorioFornecedores;
import java.util.List;

public class GerenciadorFornecedor implements IGerenciadorFornecedor {

    private IRepositorioFornecedores repositorio;

    public GerenciadorFornecedor(IRepositorioFornecedores repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void cadastrarFornecedor(Fornecedor fornecedor) throws Exception {
        if (fornecedor == null) {
            throw new Exception("O fornecedor não pode ser nulo.");
        }
        if (fornecedor.getNome() == null || fornecedor.getNome().trim().isEmpty()) {
            throw new Exception("O nome do fornecedor é obrigatório.");
        }
        if (fornecedor.getMoedaPadrao() == null) {
            throw new Exception("A moeda padrão é obrigatória.");
        }

        repositorio.cadastrar(fornecedor);
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
        repositorio.remover(id);
    }
}