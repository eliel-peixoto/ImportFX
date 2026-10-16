package dados;

import beans.Produto;
import java.util.List;

public class RepositorioProdutos extends RepositorioGenerico<Produto> implements IRepositorioProdutos {

    public RepositorioProdutos() {
        super();
    }

    @Override
    public void cadastrar(Produto produto) {
        super.cadastrar(produto); 
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
        }
    }

    @Override
    public void remover(long id) { 
        Produto p = buscarPorId(id);
        if (p != null) {
            super.removerElemento(p); 
        }
    }
}