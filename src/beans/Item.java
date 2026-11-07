package beans;

public class Item {
    private String nomeProduto;
    private Integer quantidade;
    private Double pesoTotal;
    private Double precoUnitario;
	private String simboloMoeda;

    public Item(String nomeProduto, int quantidade, double pesoTotal, double precoUnitario, String simboloMoeda) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.pesoTotal = pesoTotal;
        this.precoUnitario = precoUnitario;
		this.simboloMoeda = simboloMoeda;
    }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    
    public Double getPesoTotal() { return pesoTotal; }
    public void setPesoTotal(double pesoTotal) { this.pesoTotal = pesoTotal; }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }

	public String getSimboloMoeda() { return simboloMoeda; }
    public void setSimboloMoeda(String simboloMoeda) { this.simboloMoeda = simboloMoeda; }

    @Override
    public String toString() {
        return quantidade + "x " + nomeProduto + " (" + pesoTotal + "kg) - R$" + precoUnitario;
    }
}