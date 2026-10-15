package beans;

public class Brinquedos extends Produto {

	private Double aliquota; // não colocar a aliquota em decimais!!!
	
	//construtor
	public Brinquedos(long id, String nome, Double precoMoedaOrigem, Double peso, Double aliquota) {
		super(id, nome, precoMoedaOrigem, peso);
		this.aliquota = aliquota;
	}
	
	public Double getAliquota() {
		return this.aliquota;
	}
	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}
	
	//metodos
	public Double calcularAliquota() {
		Double aliquota = this.getAliquota();
		Double preco = this.getPrecoMoedaOrigem();// o preço tem que estar em Real (R$).
		Double valorTotal;
		
		Double porcentagem = (aliquota / 100);// transforma a aliquota em decimal
		Double imposto = (porcentagem * preco);
		valorTotal = (imposto + preco);
		return valorTotal;
	}
	
}
