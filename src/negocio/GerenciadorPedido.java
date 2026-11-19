package negocio;

import beans.Item;
import beans.Pedido;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import dados.IRepositorioPedidos;
import dados.Repositorios;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class GerenciadorPedido implements IGerenciadorPedido {

    private static GerenciadorPedido instance;
    private final IRepositorioPedidos repositorio;

    private GerenciadorPedido(IRepositorioPedidos repositorio) {
        this.repositorio = repositorio;
    }

    public static synchronized GerenciadorPedido getInstance() {
        if (instance == null) {
            instance = new GerenciadorPedido(Repositorios.pedidos());
        }
        return instance;
    }

    @Override
    public Long gerarProximoId() {
        List<Pedido> todos = repositorio.listarTodos();
        long proximoPorQuantidade = (todos == null ? 0 : todos.size()) + 1L;
        long maiorId = 0L;
        if (todos != null) {
            for (Pedido p : todos) {
                if (p.getId() != null && p.getId() > maiorId) {
                    maiorId = p.getId();
                }
            }
        }
        return Math.max(proximoPorQuantidade, maiorId + 1L);
    }

    @Override
    public void criarPedido(Pedido pedido) throws Exception {
        if (pedido == null) {
            throw new Exception("O pedido não pode ser nulo.");
        }
        if (pedido.getCliente() == null) {
            throw new Exception("Não é possível criar um pedido sem um cliente associado.");
        }
        if (pedido.getItemPedido() == null || pedido.getItemPedido().isEmpty()) {
            throw new Exception("O pedido deve conter pelo menos um item.");
        }

        //Atribui automaticamente o ID sequencial com base nos pedidos existentes
        pedido.setId(gerarProximoId());
        repositorio.cadastrar(pedido);
    }

    @Override
    public Pedido buscarPedido(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Pedido> listarPedidos() {
        return repositorio.listarTodos();
    }

    @Override
    public void atualizarStatusPedido(Pedido pedido) throws Exception {
        if (pedido == null || pedido.getId() == null) {
            throw new Exception("Pedido inválido.");
        }
        Pedido pedidoExistente = buscarPedido(pedido.getId());
        if (pedidoExistente == null) {
            throw new Exception("Pedido não encontrado no sistema.");
        }

        String statusAnterior = pedidoExistente.getStatus();
        String novoStatus = pedido.getStatus();

        pedidoExistente.setStatus(novoStatus);
        repositorio.atualizar(pedidoExistente);

        //Gera notificação para o cliente sempre que o status realmente mudar
        if (pedidoExistente.getCliente() != null
                && novoStatus != null
                && !novoStatus.equalsIgnoreCase(statusAnterior)) {
            String aviso = "O status do seu Pedido #" + pedidoExistente.getId()
                    + " foi atualizado de \"" + statusAnterior + "\" para \"" + novoStatus + "\".";
            pedidoExistente.getCliente().adicionarNotificacao(aviso, pedidoExistente.getId());
            if (dados.Repositorios.clientes() != null) {
                dados.Repositorios.clientes().atualizar(pedidoExistente.getCliente());
            }
        }
    }

    @Override
    public void cancelarPedido(Long id) throws Exception {
        Pedido pedido = buscarPedido(id);
        if (pedido == null) {
            throw new Exception("Pedido não encontrado.");
        }
        repositorio.remover(id);
    }

    @Override
    public double calcularFreteInternacional(Pedido pedido) {
        return pedido == null ? 0.0 : pedido.getValorFreteInternacional();
    }

    @Override
    public double calcularTotalImpostos(Pedido pedido) {
        return pedido == null ? 0.0 : pedido.getTotalImpostos();
    }

    @Override
    public double calcularTotalFinal(Pedido pedido) {
        return pedido == null ? 0.0 : pedido.getValorTotalFinal();
    }

    @Override
    public void exportarPedidoParaHtml(Long id, String caminhoPasta) throws Exception {
        Pedido pedido = buscarPedido(id);
        if (pedido == null) {
            throw new Exception("Pedido não encontrado para exportação.");
        }

        Locale ptBR = Locale.forLanguageTag("pt-BR");
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataStr = pedido.getDataCriacao() != null ? pedido.getDataCriacao().format(fmtData) : "-";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>");
        html.append("<title>Extrato do Pedido #").append(pedido.getId()).append("</title>");

        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
        html.append("h1 { color: #ff4501; border-bottom: 2px solid #ff4501; padding-bottom: 10px; }");
        html.append(".info-box { background-color: #f9f9f9; padding: 15px; border-left: 5px solid #ff4501; margin-bottom: 20px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }");
        html.append("th { background-color: #ff4501; color: white; }");
        html.append(".resumo { margin-top: 25px; width: 420px; margin-left: auto; border: 1px solid #ddd; padding: 15px; background: #fffaf8; }");
        html.append(".resumo p { display: flex; justify-content: space-between; margin: 6px 0; }");
        html.append(".detalhe-frete { font-size: 12px; color: #666; margin-top: -2px; margin-bottom: 8px; }");
        html.append(".total-final { font-size: 18px; color: #ff4501; border-top: 2px solid #ff4501; padding-top: 8px; margin-top: 8px; }");
        html.append("</style></head><body>");

        html.append("<h1>Extrato Detalhado - Pedido #").append(pedido.getId()).append("</h1>");
        html.append("<div class='info-box'>");
        html.append("<p><strong>Data de Emissão:</strong> ").append(dataStr).append("</p>");
        html.append("<p><strong>Status:</strong> ").append(pedido.getStatus()).append("</p>");
        html.append("<p><strong>Cliente:</strong> ID ").append(pedido.getCliente().getId())
            .append(" | ").append(pedido.getCliente().getNome()).append("</p>");
        html.append("<p><strong>Fornecedor:</strong> ID ").append(pedido.getFornecedor().getId())
            .append(" | ").append(pedido.getFornecedor().getNome())
            .append(" (País: ").append(pedido.getFornecedor().getPaisOrigem()).append(")</p>");

        if (pedido.getCotacao() != null && !pedido.getCotacao().getMoeda().isReal()) {
            html.append("<p><strong>Cotação PTAX utilizada:</strong> 1 ").append(pedido.getCotacao().getMoeda())
                .append(" = R$ ").append(String.format(ptBR, "%.4f", pedido.getCotacao().getVenda()))
                .append(" (").append(pedido.getCotacao().getDataHora().toLocalDate()).append(")")
                .append(pedido.getCotacao().isDesatualizada() ? " - cotação do cache" : "")
                .append("</p>");
        } else {
            html.append("<p><strong>Cotação utilizada:</strong> Moeda Nacional (BRL - sem conversão cambial)</p>");
        }
        html.append("</div>");

        html.append("<h2>Detalhamento dos Itens e Impostos</h2>");
        html.append("<table>");
        html.append("<tr><th>Produto</th><th>Categoria</th><th>Qtd</th><th>Peso (kg)</th>")
            .append("<th>Preço Unit.</th><th>Subtotal</th><th>Alíquota</th><th>Imposto</th></tr>");

        for (Item item : pedido.getItemPedido()) {
            double subtotalItem = item.getQuantidade() * item.getPrecoUnitario();
            double impostoItem = pedido.calcularImpostoItem(item);
            double aliqExibicao = (item.getAliquota() != null && item.getAliquota() <= 1.0)
                    ? item.getAliquota() * 100.0
                    : (item.getAliquota() != null ? item.getAliquota() : 0.0);

            html.append("<tr>")
                .append("<td>").append(item.getNomeProduto()).append("</td>")
                .append("<td>").append(item.getCategoria()).append("</td>")
                .append("<td>").append(item.getQuantidade()).append("</td>")
                .append("<td>").append(String.format(ptBR, "%.2f", item.getPesoTotal())).append("</td>")
                .append("<td>").append(item.getSimboloMoeda()).append(" ").append(String.format(ptBR, "%.2f", item.getPrecoUnitario())).append("</td>")
                .append("<td>").append(item.getSimboloMoeda()).append(" ").append(String.format(ptBR, "%.2f", subtotalItem)).append("</td>")
                .append("<td>").append(String.format(ptBR, "%.1f%%", aliqExibicao)).append("</td>")
                .append("<td>R$ ").append(String.format(ptBR, "%.2f", impostoItem)).append("</td>")
                .append("</tr>");
        }
        html.append("</table>");

        html.append("<div class='resumo'>");
        html.append("<p><span><strong>Subtotal dos Produtos:</strong></span><span>R$ ")
            .append(String.format(ptBR, "%.2f", pedido.getSubtotalItens())).append("</span></p>");
        html.append("<p><span><strong>Total de Impostos:</strong></span><span>R$ ")
            .append(String.format(ptBR, "%.2f", pedido.getTotalImpostos())).append("</span></p>");
        html.append("<p><span><strong>Frete:</strong></span><span>R$ ")
            .append(String.format(ptBR, "%.2f", pedido.getValorFreteInternacional())).append("</span></p>");
        html.append("<div class='detalhe-frete'>")
            .append(pedido.getDetalhamentoFrete())
            .append("</div>");
        html.append("<p class='total-final'><span><strong>TOTAL GERAL:</strong></span><span><strong>R$ ")
            .append(String.format(ptBR, "%.2f", pedido.getValorTotalFinal())).append("</strong></span></p>");
        html.append("</div>");

        html.append("</body></html>");

        File pasta = (caminhoPasta == null || caminhoPasta.isBlank()) ? new File(".") : new File(caminhoPasta);
        if (!pasta.exists()) pasta.mkdirs();

        File arquivoHtml = new File(pasta, "Pedido_" + pedido.getId() + ".html");
        try (FileWriter writer = new FileWriter(arquivoHtml)) {
            writer.write(html.toString());
        } catch (IOException e) {
            throw new Exception("Erro ao gravar o ficheiro HTML: " + e.getMessage());
        }

        //Abre automaticamente no navegador padrão
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(arquivoHtml.toURI());
        }
    }

    @Override
    public File exportarPedidoParaPdf(Long id, String caminhoPasta) throws Exception {
        Pedido pedido = buscarPedido(id);
        if (pedido == null) {
            throw new Exception("Pedido não encontrado para geração de PDF.");
        }

        Locale ptBR = Locale.forLanguageTag("pt-BR");
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataStr = pedido.getDataCriacao() != null ? pedido.getDataCriacao().format(fmtData) : "-";

        File pasta = (caminhoPasta == null || caminhoPasta.isBlank()) ? new File(".") : new File(caminhoPasta);
        if (!pasta.exists()) pasta.mkdirs();

        File arquivoPdf = new File(pasta, "Fatura_Pedido_" + pedido.getId() + ".pdf");

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try (FileOutputStream fos = new FileOutputStream(arquivoPdf)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            Font fonteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(255, 69, 1));
            Font fonteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font fonteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font fonteDetalhe = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8.5f, Color.DARK_GRAY);
            Font fonteNegrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font fonteCabecalhoTabela = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

            Paragraph titulo = new Paragraph("ImportFX - Fatura Final do Pedido #" + pedido.getId(), fonteTitulo);
            titulo.setSpacingAfter(12);
            document.add(titulo);

            document.add(new Paragraph("Data de Emissão: " + dataStr + "   |   Status: " + pedido.getStatus(), fonteNormal));
            document.add(new Paragraph("Cliente: ID " + pedido.getCliente().getId() + " - " + pedido.getCliente().getNome(), fonteNormal));
            document.add(new Paragraph("Fornecedor: ID " + pedido.getFornecedor().getId() + " - "
                    + pedido.getFornecedor().getNome() + " (" + pedido.getFornecedor().getPaisOrigem() + ")", fonteNormal));

            if (pedido.getCotacao() != null && !pedido.getCotacao().getMoeda().isReal()) {
                document.add(new Paragraph(String.format(ptBR, "Cotação PTAX utilizada: 1 %s = R$ %.4f",
                        pedido.getCotacao().getMoeda(), pedido.getCotacao().getVenda()), fonteNormal));
            } else {
                document.add(new Paragraph("Cotação utilizada: Moeda Nacional (BRL)", fonteNormal));
            }

            Paragraph secItens = new Paragraph("Itens do Pedido", fonteSubtitulo);
            secItens.setSpacingBefore(14);
            secItens.setSpacingAfter(8);
            document.add(secItens);

            PdfPTable tabela = new PdfPTable(new float[]{3.2f, 1.0f, 1.2f, 1.6f, 1.6f, 1.2f, 1.6f});
            tabela.setWidthPercentage(100);

            String[] cabecalhos = {"Produto", "Qtd", "Peso (kg)", "Preço Unit.", "Subtotal", "Alíq.", "Imposto"};
            for (String col : cabecalhos) {
                PdfPCell cell = new PdfPCell(new Phrase(col, fonteCabecalhoTabela));
                cell.setBackgroundColor(new Color(255, 69, 1));
                cell.setPadding(6);
                tabela.addCell(cell);
            }

            for (Item item : pedido.getItemPedido()) {
                double subtotalItem = item.getQuantidade() * item.getPrecoUnitario();
                double impostoItem = pedido.calcularImpostoItem(item);
                double aliqExibicao = (item.getAliquota() != null && item.getAliquota() <= 1.0)
                        ? item.getAliquota() * 100.0
                        : (item.getAliquota() != null ? item.getAliquota() : 0.0);

                tabela.addCell(new Phrase(item.getNomeProduto(), fonteNormal));
                tabela.addCell(new Phrase(String.valueOf(item.getQuantidade()), fonteNormal));
                tabela.addCell(new Phrase(String.format(ptBR, "%.2f", item.getPesoTotal()), fonteNormal));
                tabela.addCell(new Phrase(String.format(ptBR, "%s %.2f", item.getSimboloMoeda(), item.getPrecoUnitario()), fonteNormal));
                tabela.addCell(new Phrase(String.format(ptBR, "%s %.2f", item.getSimboloMoeda(), subtotalItem), fonteNormal));
                tabela.addCell(new Phrase(String.format(ptBR, "%.1f%%", aliqExibicao), fonteNormal));
                tabela.addCell(new Phrase(String.format(ptBR, "R$ %.2f", impostoItem), fonteNormal));
            }
            document.add(tabela);

            Paragraph secTotais = new Paragraph("Resumo Financeiro da Fatura", fonteSubtitulo);
            secTotais.setSpacingBefore(16);
            secTotais.setSpacingAfter(6);
            document.add(secTotais);

            document.add(new Paragraph(String.format(ptBR, "Subtotal dos Itens: R$ %.2f", pedido.getSubtotalItens()), fonteNormal));
            document.add(new Paragraph(String.format(ptBR, "Total de Impostos: R$ %.2f", pedido.getTotalImpostos()), fonteNormal));
            document.add(new Paragraph(String.format(ptBR, "Frete: R$ %.2f", pedido.getValorFreteInternacional()), fonteNormal));

            Paragraph detalheFrete = new Paragraph("   (" + pedido.getDetalhamentoFrete() + ")", fonteDetalhe);
            detalheFrete.setSpacingAfter(4);
            document.add(detalheFrete);

            Paragraph totalFinal = new Paragraph(String.format(ptBR,
                    "TOTAL FINAL DA FATURA (Subtotal + Impostos + Frete): R$ %.2f",
                    pedido.getValorTotalFinal()), fonteNegrito);
            totalFinal.setSpacingBefore(8);
            totalFinal.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalFinal);

            document.close();
        }

        return arquivoPdf;
    }
}