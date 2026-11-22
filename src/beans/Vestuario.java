package beans;

public class Vestuario extends Produto {
	
	private static final Double ALIQUOTA = 35.0; //não colocar a porcentagem da aliquota em decimais.

	public Vestuario(long id, String nome, Double precoMoedaOrigem, Double peso) {
        super(id, nome, precoMoedaOrigem, peso);
    }

	public Vestuario(long id, String nome, Double precoMoedaOrigem, Double peso, Double aliquota) {
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
			
			Double porcentagem = (aliquota / 100);//transformar a aliquota em decimal
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