package dados;

import beans.Brinquedo;
import beans.Eletronico;
import beans.Produto;
import beans.Vestuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class PersistenciaJson {

    private static final String PASTA_DADOS = "dados_sistema";
    private static final Gson GSON = criarGson();

    private PersistenciaJson() {
    }

    private static Gson criarGson() {
        Gson gsonBase = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
                .create();

        JsonSerializer<Produto> produtoSerializer = (src, typeOfSrc, context) -> {
            JsonObject obj = gsonBase.toJsonTree(src).getAsJsonObject();
            obj.addProperty("tipoProduto", src.getClass().getSimpleName());
            return obj;
        };

        JsonDeserializer<Produto> produtoDeserializer = (json, typeOfT, context) -> {
            JsonObject obj = json.getAsJsonObject();
            JsonElement tipoEl = obj.get("tipoProduto");
            String tipo = (tipoEl != null) ? tipoEl.getAsString() : "";

            return switch (tipo) {
                case "Eletronico" -> gsonBase.fromJson(obj, Eletronico.class);
                case "Vestuario"  -> gsonBase.fromJson(obj, Vestuario.class);
                case "Brinquedo"  -> gsonBase.fromJson(obj, Brinquedo.class);
                default -> throw new JsonParseException("Tipo de produto desconhecido no JSON: " + tipo);
            };
        };

        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(Produto.class, produtoSerializer)
                .registerTypeAdapter(Produto.class, produtoDeserializer)
                .create();
    }

    public static <T> void salvarObjeto(String nomeArquivo, T objeto) {
        try {
            Path pasta = Paths.get(PASTA_DADOS);
            if (!Files.exists(pasta)) {
                Files.createDirectories(pasta);
            }
            Path arquivo = pasta.resolve(nomeArquivo);
            String json = GSON.toJson(objeto);
            Files.writeString(arquivo, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    public static <T> T carregarObjeto(String nomeArquivo, Type tipo) {
        try {
            Path arquivo = Paths.get(PASTA_DADOS, nomeArquivo);
            if (!Files.exists(arquivo)) {
                return null;
            }
            String json = Files.readString(arquivo, StandardCharsets.UTF_8);
            return GSON.fromJson(json, tipo);
        } catch (Exception e) {
            System.err.println("Erro ao carregar " + nomeArquivo + ": " + e.getMessage());
            return null;
        }
    }

    public static <T> void salvarLista(String nomeArquivo, List<T> lista) {
        salvarObjeto(nomeArquivo, lista);
    }

    public static <T> List<T> carregarLista(String nomeArquivo, Type tipoLista) {
        List<T> dados = carregarObjeto(nomeArquivo, tipoLista);
        return dados != null ? dados : new ArrayList<>();
    }
}