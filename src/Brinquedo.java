
public final class Brinquedo extends Produto{
//atributos
private int aliquota;
	//construtor
	public Brinquedo(String nome, int preco, Fornecedor fornecedor){
		super(nome, preco, fornecedor);
		setAliquota(5);
	}
	
	//getters e setters
	public void setAliquota(int aliquota) {
		this.aliquota = aliquota;
	}
	public int getAliquota() {
		return aliquota;
	}
	
	//metodo toString()
		@Override
		public String toString() {
			return "Produto [nome: " + this.getNome() + "; preco: " + this.getPreco() + "; Fornecedor: " + this.getFornecedor().getNome() + "; aliquota: " + this.getAliquota() + "]";
		}
		
		//metodo equals()
		@Override
		public boolean equals(Object obj) {
			boolean resultado = false;
			//checagem se o objeto é o mesmo produto, nulo ou da mesma classe
			if(this == obj) return true;
			if(obj == null && this.getClass() != obj.getClass()) return false;
			//cast (pq Object tem que ser da classe Brinquedo)
			Brinquedo o = (Brinquedo) obj;
			
			//checagem real da classe
			if(this.getNome() == o.getNome() && this.getPreco() == this.getPreco() && this.getFornecedor() == o.getFornecedor() && this.getAliquota() == o.getAliquota()) {
				resultado = true;
			}
			return resultado;
		}
	
}
