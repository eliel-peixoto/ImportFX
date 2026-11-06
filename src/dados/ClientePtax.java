package dados;

import beans.Cotacao;
import beans.Moeda;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cliente da API PTAX (Olinda/OData) do Banco Central do Brasil.
 * Pública e sem chave. Usa apenas o JDK (java.net.http), sem dependências extras.
 */
public class ClientePtax implements IClientePtax {

    private static final String BASE_URL =
            "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/";
    private static final int DIAS_PARA_VOLTAR = 7; // fim de semana/feriado não tem PTAX
    private static final DateTimeFormatter DATA_API = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final DateTimeFormatter DATA_HORA_API = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Pattern COMPRA = Pattern.compile("\"cotacaoCompra\"\\s*:\\s*([0-9.]+)");
    private static final Pattern VENDA = Pattern.compile("\"cotacaoVenda\"\\s*:\\s*([0-9.]+)");
    private static final Pattern DATA_HORA = Pattern.compile("\"dataHoraCotacao\"\\s*:\\s*\"([^\"]+)\"");

    private final HttpClient http;

    public ClientePtax() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public Cotacao consultar(Moeda moeda) throws IOException {
        LocalDate dia = LocalDate.now();
        for (int i = 0; i <= DIAS_PARA_VOLTAR; i++) {
            String json = buscarJson(moeda, dia.minusDays(i));
            Cotacao cotacao = extrairUltimaCotacao(moeda, json);
            if (cotacao != null) {
                return cotacao;
            }
        }
        throw new IOException("A API PTAX não retornou cotação para " + moeda
                + " nos últimos " + DIAS_PARA_VOLTAR + " dias.");
    }

    private String buscarJson(Moeda moeda, LocalDate data) throws IOException {
        String url = BASE_URL
                + "CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)"
                + "?@moeda=%27" + moeda.name() + "%27"
                + "&@dataCotacao=%27" + DATA_API.format(data) + "%27"
                + "&$top=100&$format=json";

        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try {
            HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() != 200) {
                throw new IOException("API PTAX respondeu com status " + resposta.statusCode());
            }
            return resposta.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Consulta à API PTAX interrompida.", e);
        }
    }

    /**
     * O dia pode ter vários boletins (abertura, intermediários e fechamento).
     * Pegamos o último de cada campo, que é o mais recente.
     * Retorna null se a resposta vier vazia (dia sem cotação).
     */
    
    private Cotacao extrairUltimaCotacao(Moeda moeda, String json) throws IOException {
        Double compra = ultimoNumero(COMPRA, json);
        Double venda = ultimoNumero(VENDA, json);
        if (compra == null || venda == null) {
            return null;
        }
        LocalDateTime dataHora = LocalDateTime.now();
        String textoData = ultimoTexto(DATA_HORA, json);
        if (textoData != null && textoData.length() >= 19) {
            try {
                dataHora = LocalDateTime.parse(textoData.substring(0, 19), DATA_HORA_API);
            } catch (RuntimeException e) {
                throw new IOException("Data/hora inesperada na resposta da API PTAX: " + textoData, e);
            }
        }
        return new Cotacao(moeda, compra, venda, dataHora);
    }

    private Double ultimoNumero(Pattern padrao, String json) {
        String texto = ultimoTexto(padrao, json);
        return texto == null ? null : Double.valueOf(texto);
    }

    private String ultimoTexto(Pattern padrao, String json) {
        Matcher m = padrao.matcher(json);
        String ultimo = null;
        while (m.find()) {
            ultimo = m.group(1);
        }
        return ultimo;
    }
}