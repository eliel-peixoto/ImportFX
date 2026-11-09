package negocio;

import beans.Pedido;
import dados.IRepositorioPedidos;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class GerenciadorPedido implements IGerenciadorPedido {

    private IRepositorioPedidos repositorio;

    public GerenciadorPedido(IRepositorioPedidos repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void criarPedido(Pedido pedido) throws Exception {
        if (pedido == null) {
            throw new Exception("O pedido não pode ser nulo.");
        }
        if (pedido.getCliente() == null) {
            throw new Exception("Não é possível criar um pedido sem um cliente associado.");
        }
        //Verifica se a lista de itens existe e não está vazia
        if (pedido.getItemPedido() == null || pedido.getItemPedido().isEmpty()) {
            throw new Exception("O pedido deve conter pelo menos um item.");
        }

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
        
        //Atualiza apenas o status e outros dados permitidos
        pedidoExistente.setStatus(pedido.getStatus());
        repositorio.atualizar(pedidoExistente);
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
    public void exportarPedidoParaHtml(Long id, String caminhoPasta) throws Exception {
        Pedido pedido = buscarPedido(id);
        if (pedido == null) {
            throw new Exception("Pedido não encontrado para exportação.");
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>");
        html.append("<title>Extrato do Pedido #").append(pedido.getId()).append("</title>");
        
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
        html.append("h1 { color: #ff4501; border-bottom: 2px solid #ff4501; padding-bottom: 10px; }");
        html.append(".info-box { background-color: #f9f9f9; padding: 15px; border-left: 5px solid #ff4501; margin-bottom: 20px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
        html.append("th { background-color: #ff4501; color: white; }");
        html.append("</style></head><body>");

        html.append("<h1>Nota Fiscal - Pedido #").append(pedido.getId()).append("</h1>");
        html.append("<div class='info-box'>");
        html.append("<p><strong>Status:</strong> ").append(pedido.getStatus()).append("</p>");
    
        html.append("<p><strong>Cliente:</strong> ID ").append(pedido.getCliente().getId())
            .append(" | ").append(pedido.getCliente().getNome()).append("</p>");
            
        html.append("<p><strong>Fornecedor:</strong> ID ").append(pedido.getFornecedor().getId())
            .append(" | ").append(pedido.getFornecedor().getNome())
            .append(" (País: ").append(pedido.getFornecedor().getPaisOrigem()).append(")</p>");

        if (pedido.getCotacao() != null && !pedido.getCotacao().getMoeda().isReal()) {
            html.append("<p><strong>Cotação PTAX utilizada:</strong> 1 ").append(pedido.getCotacao().getMoeda())
                .append(" = R$ ").append(String.format("%.4f", pedido.getCotacao().getVenda()))
                .append(" (").append(pedido.getCotacao().getDataHora().toLocalDate()).append(")")
                .append(pedido.getCotacao().isDesatualizada() ? " - cotação do cache" : "")
                .append("</p>");
        }

        html.append("</div>");

        html.append("<h2>Lista de Itens</h2>");
        html.append("<table>");
        html.append("<tr><th>Produto</th><th>Quantidade</th><th>Peso Total (kg)</th><th>Preço Unit.</th><th>Subtotal</th></tr>");

        double valorTotalPedido = 0.0;
        String simboloTotal = "R$"; // Padrão

        for (beans.Item item : pedido.getItemPedido()) {
            double subtotal = item.getQuantidade() * item.getPrecoUnitario();
            valorTotalPedido += subtotal;
            simboloTotal = item.getSimboloMoeda(); 
            
            html.append("<tr>")
                .append("<td>").append(item.getNomeProduto()).append("</td>")
                .append("<td>").append(item.getQuantidade()).append("</td>")
                .append("<td>").append(item.getPesoTotal()).append("</td>")
                .append("<td>").append(item.getSimboloMoeda()).append(" ").append(String.format("%.2f", item.getPrecoUnitario())).append("</td>")
                .append("<td>").append(item.getSimboloMoeda()).append(" ").append(String.format("%.2f", subtotal)).append("</td>")
                .append("</tr>");
        }
        
        html.append("<tr>")
            .append("<td colspan='4' style='text-align: right; font-size: 18px;'><strong>TOTAL DO PEDIDO:</strong></td>")
            .append("<td style='font-size: 18px; color: #ff4501;'><strong>")
            .append(simboloTotal).append(" ").append(String.format("%.2f", valorTotalPedido))
            .append("</strong></td>")
            .append("</tr>");

        html.append("</table>");
        html.append("</body></html>");
        
        String caminhoCompleto = caminhoPasta + "/Pedido_" + pedido.getId() + ".html";
        try (FileWriter writer = new FileWriter(caminhoCompleto)) {
            writer.write(html.toString());
        } catch (IOException e) {
            throw new Exception("Erro ao gravar o ficheiro HTML: " + e.getMessage());
        }
    }
}