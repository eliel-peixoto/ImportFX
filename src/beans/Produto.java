package beans;

public abstract class Produto {
	//atributos
	private long id;
	private String nome;
	private Double precoMoedaOrigem;
	private Double peso;
	
	//construtor
	public Produto(long id, String nome, Double precoMoedaOrigem, Double peso) {
		this.setId(id);
		this.nome = nome;
		this.precoMoedaOrigem = precoMoedaOrigem;
		this.peso = peso;
	}

	//getters e setters
	public long getId() {
		return id;
	}

	public void setId(long id) {//dois produtos do mesmo fornecedor não podem ter o mesmo ID;
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getPrecoMoedaOrigem() {
		return precoMoedaOrigem;
	}

	public void setPrecoMoedaOrigem(Double precoMoedaOrigem) {
		this.precoMoedaOrigem = precoMoedaOrigem;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}
	
	//toString
	public String toString() {
		return String.format("""
				[PRODUTO]
				Nome:  %s
				Id:    %d
				Preço: %.2f
				peso:  %.2f
				""", this.getNome(), this.getId(), this.getPrecoMoedaOrigem(), this.getPeso());
	}
	
	//metodo equals()
	@Override
	public boolean equals(Object obj) {
		boolean resultado = false;
		//checagem se o objeto é o mesmo produto, nulo ou da mesma classe
		if(this == obj) return true;
		if(obj == null || this.getClass() != obj.getClass()) return false;
		//cast (pq Object tem que ser da classe Produto)
		Produto o = (Produto) obj;
		
		//checagem real da classe
		if(this.getPrecoMoedaOrigem() == o.getPrecoMoedaOrigem() && this.getId() == o.getId() && this.getNome() == o.getNome() && this.getPeso() == o.getPeso()) {
			resultado = true;
		}
		return resultado;
	}
	

}
