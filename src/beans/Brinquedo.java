package beans;

public class Brinquedo extends Produto {

	private static final Double ALIQUOTA = 20.0; //não colocar a aliquota em decimais!!!
	
	//construtor
	public Brinquedo(long id, String nome, Double precoMoedaOrigem, Double peso) {
		super(id, nome, precoMoedaOrigem, peso);
	}

	public Brinquedo(long id, String nome, Double precoMoedaOrigem, Double peso, Double aliquota) {
		this(id, nome, precoMoedaOrigem, peso);
	}
	
	@Override
	public Double getAliquota() {
		return ALIQUOTA;
	}
	
	//metodos
	public Double calcularAliquota() {
		Double aliquota = this.getAliquota();
		Double preco = this.getPrecoMoedaOrigem();//o preço tem que estar em Real (R$).
		Double valorTotal;
		
		Double porcentagem = (aliquota / 100);//transforma a aliquota em decimal
		Double imposto = (porcentagem * preco);
		valorTotal = (imposto + preco);
		return valorTotal;
	}
	@Override
    public Double calcularImposto() {
		Double preco = this.getPrecoMoedaOrigem();
		if (preco == null) {
			return 0.0;
		}
		return (this.getAliquota() / 100.0) * preco;
    }

}