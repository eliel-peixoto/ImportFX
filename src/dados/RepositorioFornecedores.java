package dados;

import beans.Fornecedor;
import java.util.List;

public class RepositorioFornecedores extends RepositorioGenerico<Fornecedor> implements IRepositorioFornecedores {

    public RepositorioFornecedores() {
        super();
    }

    @Override
    public void cadastrar(Fornecedor fornecedor) {
        super.cadastrar(fornecedor);
    }

    @Override
    public List<Fornecedor> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public Fornecedor buscarPorId(long id) {
        for (Fornecedor f : elementos) {
            if (f.getId() != null && f.getId() == id) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Fornecedor fornecedorAtualizado) {
        if (fornecedorAtualizado != null && fornecedorAtualizado.getId() != null) {
            Fornecedor antigo = buscarPorId(fornecedorAtualizado.getId());
            if (antigo != null) {
                antigo.setNome(fornecedorAtualizado.getNome());
                antigo.setPaisOrigem(fornecedorAtualizado.getPaisOrigem());
                antigo.setMoedaPadrao(fornecedorAtualizado.getMoedaPadrao());
            }
        }
    }

    @Override
    public void remover(long id) {
        Fornecedor f = buscarPorId(id);
        if (f != null) {
            super.removerElemento(f);
        }
    }

}