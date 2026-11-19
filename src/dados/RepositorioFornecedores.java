package dados;

import beans.Fornecedor;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class RepositorioFornecedores extends RepositorioGenerico<Fornecedor> implements IRepositorioFornecedores {

    private static final String ARQUIVO = "fornecedores.json";

    public RepositorioFornecedores() {
        super();
        Type tipoLista = new TypeToken<List<Fornecedor>>() {}.getType();
        this.elementos.addAll(PersistenciaJson.carregarLista(ARQUIVO, tipoLista));

        long maiorId = 0;
        for (Fornecedor f : this.elementos) {
            if (f.getId() != null && f.getId() > maiorId) {
                maiorId = f.getId();
            }
        }
        Fornecedor.sincronizarSequenciaId(maiorId);
    }

    public void salvar() {
        PersistenciaJson.salvarLista(ARQUIVO, this.elementos);
    }

    @Override
    public void cadastrar(Fornecedor fornecedor) {
        super.cadastrar(fornecedor);
        salvar();
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
                salvar();
            }
        }
    }

    @Override
    public void remover(long id) {
        Fornecedor f = buscarPorId(id);
        if (f != null) {
            super.removerElemento(f);
            salvar();
        }
    }
}