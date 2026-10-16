package beans;

public class Vestuario extends Produto {
	
	private Double aliquota; //não colocar a porcentagem da aliquota em decimais.

	public Vestuario(long id, String nome, Double precoMoedaOrigem, Double peso) {
        super(id, nome, precoMoedaOrigem, peso);
        this.aliquota = 0.0; //valor padrão para aliquota
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
			Double preco = this.getPrecoMoedaOrigem();//o preço tem que estar em Real (R$).
			Double valorTotal;
			
			Double porcentagem = (aliquota / 100);//transformar a aliquota em decimal
			Double imposto = (porcentagem * preco);
			valorTotal = (imposto + preco);
			return valorTotal;
		}
	@Override
	public Double calcularImposto() {   
		return 0.0; //Retorno provisório apenas para o código voltar a compilar
	}
}
