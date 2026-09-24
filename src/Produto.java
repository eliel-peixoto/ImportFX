
public abstract class Produto {
	//atributos
	private int preco;
	private Fornecedor fornecedor;
	
	//construtor
	public Produto(int preco, Fornecedor fornecedor){
		setPreco(preco);
		setFornecedor(fornecedor);
	}
	
	//getters e setters
	public int getPreco() {
		return preco;
	}
	public void setPreco(int preco) {
		this.preco = preco;
	}
	public Fornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	//toString
	@Override
	public String toString() {
		return "Produto [preco: " + this.getPreco() + "; Fornecedor: " + this.getFornecedor().getNome() + "]";
	}
	
	//metodo equals()
	@Override
	public boolean equals(Object obj) {
		boolean resultado = false;
		//checagem se o objeto é o mesmo produto, nulo ou da mesma classe
		if(this == obj) return true;
		if(obj == null && this.getClass() != obj.getClass()) return false;
		//cast (pq Object tem que ser da classe Produto)
		Produto o = (Produto) obj;
		
		//checagem real da classe
		if(this.getPreco() == o.getPreco() && this.getFornecedor() == o.getFornecedor()) {
			resultado = true;
		}
		return resultado;
	}
	

}
