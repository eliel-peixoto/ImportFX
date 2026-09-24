package beans;
public class Item {
	//atributos
    private String nomeProduto;
    private Integer quantidade;
    private Integer pesoTotal;

    //construtor
    public Item(String nomeProduto, int quantidade, int pesoTotal) {
    	setNomeProduto(nomeProduto);
    	setQuantidade(quantidade);
    	setPesoTotal(pesoTotal);
    }

    //getters e setters
	public String getNomeProduto() {
		return nomeProduto;
	}

	public void setNomeProduto(String nomeProduto) {
		this.nomeProduto = nomeProduto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		Integer a = (Integer) quantidade;
		this.quantidade = a;
	}

	public Integer getPesoTotal() {
		return pesoTotal;
	}

	public void setPesoTotal(int pesoTotal) {
		Integer b = (Integer) pesoTotal;
		this.pesoTotal = b;
	}

}
