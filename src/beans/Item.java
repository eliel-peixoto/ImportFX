package beans;

public class Item {
    private String nomeProduto;
    private Integer quantidade;
    private Double pesoTotal;
    private Double precoUnitario;
	private String simboloMoeda;
    private Long idProduto;
    private String categoria;
    private Double aliquota;

    public Item(String nomeProduto, int quantidade, double pesoTotal, double precoUnitario, String simboloMoeda) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.pesoTotal = pesoTotal;
        this.precoUnitario = precoUnitario;
		this.simboloMoeda = simboloMoeda;
        this.categoria = "Não informada";
        this.aliquota = 0.0;
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

    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getAliquota() { return aliquota; }
    public void setAliquota(Double aliquota) { this.aliquota = aliquota; }

    public boolean isConvertidoParaReal() {
        return "R$".equals(simboloMoeda);
    }

    @Override
    public String toString() {
        return quantidade + "x " + nomeProduto + " (" + pesoTotal + "kg) - "
                + simboloMoeda + " " + precoUnitario;
    }
}
