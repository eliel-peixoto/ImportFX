package dados;

import beans.Produto;
import java.util.List;

public class RepositorioProdutos extends RepositorioGenerico<Produto> implements IRepositorioProdutos {

    public RepositorioProdutos() {
        super();
    }

    // O @Override prova ao Java que estamos a cumprir o contrato da Interface
    @Override
    public void cadastrar(Produto produto) {
        super.cadastrar(produto); // Usa a lógica do Repositório Genérico
    }

    @Override
    public List<Produto> listarTodos() {
        return super.listarTodos(); // Usa a lógica do Repositório Genérico
    }

    @Override
    public Produto buscarPorId(long id) { // Corrigido para 'long' minúsculo
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
    public void remover(long id) { // Corrigido para 'long' minúsculo
        Produto p = buscarPorId(id);
        if (p != null) {
            super.removerElemento(p); // Usa a lógica do Repositório Genérico
        }
    }
}