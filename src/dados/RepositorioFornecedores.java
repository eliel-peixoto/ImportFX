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
            if (f.getId() == id) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void remover(long id) {
        Fornecedor f = buscarPorId(id);
        if (f != null) {
            super.removerElemento(f);
        }
    }
}