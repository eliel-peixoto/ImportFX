
public final class CategoriaProduto extends Produto {
	//atributos
	private String categoria;
	private int quantidade;
	private int aliquota;
	private int peso;

	//construtor
	private CategoriaProduto(int preco, Fornecedor fornecedor, String categoria, int quantidade, int aliquota, int peso){
		super(preco, fornecedor);
		
		setCategoria(categoria);
		setQuantidade(quantidade);
		setAliquota(aliquota);
    setPeso(peso);
	}

	//getters e setters
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	public int getAliquota() {
		return aliquota;
	}
	
	public void setAliquota(int aliquota) {
		this.aliquota = aliquota;
	}
	
	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}
	
	//toString
	@Override
	public String toString() {
		return "Produto [preco: " + this.getPreco() + "; Fornecedor: " + this.getFornecedor().getNome() + "; categoria: " + this.getCategoria() + "; quantidade: " + this.getQuantidade() + "; aliquota: " + this.getAliquota() + "; peso: " + this.getPeso() + "]";
	}
	
	//equals
	@Override
	public boolean equals(Object obj) {
		boolean resultado = false;
		//checagem se o objeto é o mesmo produto, nulo ou da mesma classe
		if(this == obj) return true;
		if(obj == null && this.getClass() != obj.getClass()) return false;
		//cast (pq Object tem que ser da classe CategoriaProduto)
		CategoriaProduto o = (CategoriaProduto) obj;
		
		//checagem real da classe
		if(this.getPreco() == o.getPreco() && this.getFornecedor() == o.getFornecedor() && this.getCategoria() == o.getCategoria() && this.getQuantidade() == o.getQuantidade() && this.getAliquota() == o.getAliquota() && this.getPeso() == o.getPeso()) {
			resultado = true;
		}
		return resultado;
	}
}
