package dados;

import java.util.List;
import java.util.ArrayList;
import beans.Produto;

public class RepositorioProdutos implements IRepositorioProdutos {
	
	private ArrayList<Produto> listaProduto;
	
	//metodos da interface
	@Override
	public void adicionarProduto(Produto p) {
		this.listaProduto.add(p);
	}
	
	@Override
	public Produto procurarProduto(int id, String nome) {//procura por id e nome
		for(Produto p : listaProduto) {
			if(p.getId() == id && p.getNome() == nome) {
				return p;
			}
		}
		return null;
	}
	
	@Override
	public void removerProduto(int id, String nome) {//remove usando o metodo de procura
		Produto pEncontrado = procurarProduto(id, nome);
		if(pEncontrado != null) {
			this.listaProduto.remove(pEncontrado);
		}
	}
	
	@Override
	public List<Produto> listarTodos() {
		return new ArrayList<Produto>(this.listaProduto);
	}

}
