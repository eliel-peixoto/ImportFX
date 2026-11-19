package dados;

import beans.Produto;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class RepositorioProdutos extends RepositorioGenerico<Produto> implements IRepositorioProdutos {

    private static final String ARQUIVO = "produtos.json";
    private final boolean persistir;

    public RepositorioProdutos() {
        this(true);
    }

    public RepositorioProdutos(boolean persistir) {
        super();
        this.persistir = persistir;
        if (persistir) {
            Type tipoLista = new TypeToken<List<Produto>>() {}.getType();
            this.elementos.addAll(PersistenciaJson.carregarLista(ARQUIVO, tipoLista));
        }
    }

    private void salvar() {
        if (persistir) {
            PersistenciaJson.salvarLista(ARQUIVO, this.elementos);
        }
    }

    @Override
    public void cadastrar(Produto produto) {
        super.cadastrar(produto);
        salvar();
    }

    @Override
    public List<Produto> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public Produto buscarPorId(long id) {
        for (Produto p : elementos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Produto produtoAtualizado) {
        Produto antigo = buscarPorId(produtoAtualizado.getId());
        if (antigo != null) {
            antigo.setNome(produtoAtualizado.getNome());
            antigo.setPrecoMoedaOrigem(produtoAtualizado.getPrecoMoedaOrigem());
            antigo.setPeso(produtoAtualizado.getPeso());
            antigo.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            salvar();
        }
    }

    @Override
    public void remover(long id) {
        Produto p = buscarPorId(id);
        if (p != null) {
            super.removerElemento(p);
            salvar();
        }
    }
}