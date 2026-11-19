package dados;

import beans.Cotacao;
import beans.Moeda;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cliente da API PTAX (Olinda/OData) do Banco Central do Brasil.
 * Pública e sem chave. Usa java.net.http para a requisição e Gson para ler o JSON.
 */
public class ClientePtax implements IClientePtax {

    private static final String BASE_URL =
            "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/";
    private static final int DIAS_PARA_VOLTAR = 7; // fim de semana/feriado não tem PTAX
    private static final DateTimeFormatter DATA_API = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final DateTimeFormatter DATA_HORA_API = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HttpClient http;

    public ClientePtax() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public Cotacao consultar(Moeda moeda) throws IOException {
        if (moeda == null) {
            throw new IOException("A moeda não pode ser nula.");
        }
        if (!moeda.temPtax()) {
            // Defesa extra: nem chega a fazer requisição para moeda que o PTAX não cobre.
            throw new IOException("O PTAX do Banco Central não publica cotação de " + moeda + ".");
        }
        LocalDate dia = LocalDate.now();
        for (int i = 0; i <= DIAS_PARA_VOLTAR; i++) {
            String json = buscarJson(moeda, dia.minusDays(i));
            Cotacao cotacao = interpretar(moeda, json);
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

    public static Cotacao interpretar(Moeda moeda, String json) throws IOException {
        JsonObject raiz = lerRaiz(json);
        JsonElement valor = raiz.get("value");
        if (valor == null || !valor.isJsonArray()) {
            throw new IOException("Resposta inesperada da API PTAX (sem o array 'value').");
        }
        JsonArray boletins = valor.getAsJsonArray();
        Cotacao ultima = null;
        for (JsonElement item : boletins) {
            if (!item.isJsonObject()) {
                throw new IOException("Boletim inesperado na resposta da API PTAX.");
            }
            ultima = lerBoletim(moeda, item.getAsJsonObject());
        }
        return ultima;
    }

    private static JsonObject lerRaiz(String json) throws IOException {
        if (json == null) {
            throw new IOException("Resposta vazia da API PTAX.");
        }
        try {
            JsonElement raiz = JsonParser.parseString(json);
            if (!raiz.isJsonObject()) {
                throw new IOException("Resposta inesperada da API PTAX (não é um objeto JSON).");
            }
            return raiz.getAsJsonObject();
        } catch (JsonParseException e) {
            throw new IOException("JSON inválido na resposta da API PTAX: " + e.getMessage(), e);
        }
    }

    private static Cotacao lerBoletim(Moeda moeda, JsonObject boletim) throws IOException {
        BigDecimal compra = numero(boletim, "cotacaoCompra");
        BigDecimal venda = numero(boletim, "cotacaoVenda");
        String textoData = texto(boletim, "dataHoraCotacao");
        if (textoData.length() < 19) {
            throw new IOException("Campo 'dataHoraCotacao' inesperado na resposta da API PTAX: " + textoData);
        }
        try {
            LocalDateTime dataHora = LocalDateTime.parse(textoData.substring(0, 19), DATA_HORA_API);
            return new Cotacao(moeda, compra, venda, dataHora);
        } catch (RuntimeException e) { // DateTimeParseException ou cotação <= 0
            throw new IOException("Boletim inválido na resposta da API PTAX: " + e.getMessage(), e);
        }
    }

    private static BigDecimal numero(JsonObject boletim, String nome) throws IOException {
        JsonElement campo = boletim.get(nome);
        if (campo == null || !campo.isJsonPrimitive()) {
            throw new IOException("Campo '" + nome + "' ausente ou malformado na resposta da API PTAX.");
        }
        try {
            return campo.getAsBigDecimal();
        } catch (NumberFormatException e) {
            throw new IOException("Campo '" + nome + "' não é numérico na resposta da API PTAX.", e);
        }
    }

    private static String texto(JsonObject boletim, String nome) throws IOException {
        JsonElement campo = boletim.get(nome);
        if (campo == null || !campo.isJsonPrimitive()) {
            throw new IOException("Campo '" + nome + "' ausente ou malformado na resposta da API PTAX.");
        }
        return campo.getAsString();
    }
}