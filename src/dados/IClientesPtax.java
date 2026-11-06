package dados;

import beans.Cotacao;
import beans.Moeda;
import java.io.IOException;

public interface IClientePtax {
    /**
     * Consulta a cotação mais recente da moeda na API PTAX.
     * @throws IOException
     */
    Cotacao consultar(Moeda moeda) throws IOException;
}