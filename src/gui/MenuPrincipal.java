package gui;

import beans.Vestuario;
import beans.Eletronico;
import beans.Brinquedo;
import beans.Produto;
import beans.Cliente;       
import beans.Fornecedor;
import beans.Moeda; 
import beans.Pedido;
import beans.Item;

import dados.RepositorioProdutos;
import dados.ClientePtax;
import dados.RepositorioCliente;    
import dados.RepositorioFornecedores; 
import dados.RepositorioPedidos;
import dados.RepositorioCotacoes;

import negocio.GerenciadorProduto;
import negocio.GerenciadorCadastro;
import negocio.GerenciadorCambio;
import negocio.IGerenciadorCambio;
import negocio.GerenciadorPedido;
import negocio.IGerenciadorPedido;

import java.util.Scanner;

public class MenuPrincipal {
    
    private GerenciadorProduto controlProduto;
    private GerenciadorCadastro controlCadastro;
    private IGerenciadorCambio controlCambio;
    private IGerenciadorPedido controlPedido;
    private RepositorioCliente repoC;
    private RepositorioFornecedores repoF;
    private Scanner scanner;

    public MenuPrincipal() {
        RepositorioProdutos repoProdutos = new RepositorioProdutos();
        this.controlProduto = new GerenciadorProduto(repoProdutos);
        
        this.repoC = new RepositorioCliente();
        this.repoF = new RepositorioFornecedores();
        this.controlCadastro = new GerenciadorCadastro(repoC, repoF);
        
        RepositorioPedidos repoP = new RepositorioPedidos();
        this.controlPedido = new GerenciadorPedido(repoP);
        
        this.scanner = new Scanner(System.in);
        this.controlCambio = new GerenciadorCambio(new ClientePtax(), new RepositorioCotacoes());
    }

    public void iniciar() {
        int opcao = -1;
        System.out.println("=== Boas Vindas ao ImportFX ===");

        while (opcao != 0) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Cadastrar Produto (Vestuário, Eletrônico ou Brinquedo)");
            System.out.println("2. Listar Todos os Produtos");
            System.out.println("3. Remover Produto");
            System.out.println("4. Cadastrar Cliente");
            System.out.println("5. Cadastrar Fornecedor");
            System.out.println("6. Fazer uma Compra / Pedido");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
                
                switch (opcao) {
                    case 1:
                        cadastrarProdutoUI();
                        break;
                    case 2:
                        listarProdutosUI();
                        break;
                    case 3:
                        removerProdutoUI();
                        break;
                    case 4:
                        cadastrarClienteUI();
                        break;
                    case 5:
                        cadastrarFornecedorUI();
                        break;
                    case 6:
                        fazerPedidoUI();
                        break;
                    case 0:
                        System.out.println("\nA encerrar o sistema ImportFX. Até logo!");
                        break;
                    default:
                        System.out.println("\nOpção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nErro: Por favor, digite apenas números válidos.");
            } catch (Exception e) {
                System.out.println("\nErro na operação: " + e.getMessage());
            }
        }
        scanner.close();
    }

   private void cadastrarProdutoUI() throws Exception {
        System.out.println("\n-- Cadastrar Produto --");
        System.out.println("Digite '-' a qualquer momento para cancelar e voltar ao menu.");
        
        if (repoF.listarTodos().isEmpty()) {
            System.out.println("Aviso: É necessário cadastrar um Fornecedor primeiro.");
            return;
        }

        System.out.println("Fornecedores disponíveis:");
        for (Fornecedor f : repoF.listarTodos()) {
            System.out.println("ID: " + f.getId() + " | Nome: " + f.getNome());
        }
        
        Long idFornec;
        while (true) {
            try {
                System.out.print("Digite o ID do Fornecedor para este produto: ");
                String inputId = scanner.nextLine().trim();
                if (inputId.equals("-")) {
                    return;
                }
                idFornec = Long.parseLong(inputId);
                if (repoF.buscarPorId(idFornec) != null) break;
                System.out.println("Erro: Fornecedor não encontrado.");
            } catch (Exception e) { System.out.println("Erro: Digite um ID numérico válido."); }
        }

        System.out.println("\nEscolha a categoria do produto:");
        System.out.println("1 - Vestuário\n2 - Eletrônico\n3 - Brinquedo");
        System.out.print("Categoria escolhida: ");
        int tipoProduto = Integer.parseInt(scanner.nextLine());
        
        if (tipoProduto < 1 || tipoProduto > 3) {
            System.out.println("Categoria inválida.");
            return;
        }

        System.out.print("ID do Produto: ");
        long id = Long.parseLong(scanner.nextLine());
        
        System.out.print("Nome do Produto: ");
        String nome = scanner.nextLine();
        
        System.out.print("Preço (Moeda Origem): ");
        Double preco = Double.parseDouble(scanner.nextLine().replace(",", "."));
        
        System.out.print("Peso (kg): ");
        Double peso = Double.parseDouble(scanner.nextLine().replace(",", "."));
        
        Produto novoProduto = null;

        if (tipoProduto == 1) {
            novoProduto = new Vestuario(id, nome, preco, peso, 0.0);
        } else if (tipoProduto == 2) {
            System.out.print("Alíquota (%): ");
            Double aliquota = Double.parseDouble(scanner.nextLine().replace(",", "."));
            novoProduto = new Eletronico(id, nome, preco, peso, aliquota);
        } else if (tipoProduto == 3) {
            System.out.print("Alíquota (%): ");
            Double aliquota = Double.parseDouble(scanner.nextLine().replace(",", "."));
            novoProduto = new Brinquedo(id, nome, preco, peso, aliquota);
        }
        
        if (novoProduto != null) {
            novoProduto.setIdFornecedor(idFornec);
            controlProduto.cadastrarProduto(novoProduto);
            System.out.println("Produto cadastrado e vinculado ao fornecedor com sucesso!");
        }
    }

    private void listarProdutosUI() {
        System.out.println("\n-- Lista de Produtos --");
        if (controlProduto.listarTodos().isEmpty()) {
            System.out.println("Nenhum produto cadastrado no momento.");
        } else {
            System.out.println(controlProduto.listarTodos());
        }
    }

    private void removerProdutoUI() throws Exception {
        System.out.println("\n-- Remover Produto --");
        System.out.print("Digite o ID do produto a remover: ");
        long idRemover = Long.parseLong(scanner.nextLine());
        controlProduto.removerProduto(idRemover);
        System.out.println("Produto removido com sucesso!");
    }

    private void cadastrarClienteUI() {
        System.out.println("\n-- Cadastrar Cliente --");
        System.out.println("Digite '-' a qualquer momento para cancelar e voltar ao menu.");

        long id;
        while (true) {
            try {
                System.out.print("ID do Cliente: ");
                String inputId = scanner.nextLine().trim();
                if (inputId.equals("-")) {
                    return;
                }
                id = Long.parseLong(inputId);
                break;
            } catch (Exception e) { System.out.println("Erro: Digite apenas números para o ID."); }
        }
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        Long cpf;
        while (true) {
            System.out.print("CPF (apenas números): ");
            String cpfStr = scanner.nextLine().trim();
            if (cpfStr.matches("\\d{11}")) {
                cpf = Long.parseLong(cpfStr);
                break;
            }
            System.out.println("Erro: O CPF deve conter exatamente 11 dígitos numéricos. Tente novamente.");
        }
        
        System.out.println("\n--- Endereço do Cliente ---");
        System.out.print("1. Estado: ");
        String estado = scanner.nextLine();
        
        System.out.print("2. Cidade: ");
        String cidade = scanner.nextLine();
        
        String cep;
        while (true) {
            System.out.print("3. CEP (apenas números): ");
            cep = scanner.nextLine().trim();
            if (cep.matches("\\d{8}")) {
                break;
            }
            System.out.println("Erro: O CEP deve conter exatamente 8 dígitos numéricos. Tente novamente.");
        }
        
        System.out.print("4. Bairro: ");
        String bairro = scanner.nextLine();

        System.out.print("5. Rua/Avenida: ");
        String rua = scanner.nextLine();
        
        String numero;
        while (true) {
            System.out.print("6. Número da casa (apto): ");
            numero = scanner.nextLine().trim();
            if (!numero.isEmpty()) break;
            System.out.println("Erro: O número não pode ficar vazio.");
        }
        
        System.out.print("7. Ponto de Referência: ");
        String ref = scanner.nextLine();
        
        //Junta tudo numa única string para guardar no Cliente
        String enderecoCompleto = rua + ", Nº " + numero + " - Bairro: " + bairro + 
                                  " | " + cidade + "/" + estado + " | CEP: " + cep + " | Ref: " + ref;
        
        Cliente novoCliente = new Cliente(id, nome, cpf, enderecoCompleto);
        try {
            controlCadastro.cadastrarCliente(novoCliente);
            System.out.println("\nCliente cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private void cadastrarFornecedorUI() {
        System.out.println("\n-- Cadastrar Fornecedor --");
        System.out.println("Digite '-' a qualquer momento para cancelar e voltar ao menu.");
        System.out.print("ID do Fornecedor: ");
        String inputId = scanner.nextLine().trim();
        if (inputId.equals("-")) {
            return;
        }
        long id = Long.parseLong(inputId);
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("País de Origem: ");
        String pais = scanner.nextLine();
        
        System.out.print("Moeda Padrão (USD, EUR, GBP, CNY): ");
        String moedaStr = scanner.nextLine().toUpperCase();
        
        if (!moedaStr.equals("USD") && !moedaStr.equals("EUR") && !moedaStr.equals("GBP") && !moedaStr.equals("CNY")) {
            System.out.println("Erro: Moeda inválida. O fornecedor deve usar USD, EUR, GBP ou CNY.");
            return;
        }
        
        try {
            Moeda moedaEnum = Moeda.valueOf(moedaStr); 
            Fornecedor novoFornecedor = new Fornecedor(id, nome, pais, moedaEnum);
            controlCadastro.cadastrarFornecedor(novoFornecedor);
            System.out.println("Fornecedor cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: A moeda digitada não existe no sistema.");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar fornecedor: " + e.getMessage());
        }
    }

    private void fazerPedidoUI() {
        System.out.println("\n-- Criar Novo Pedido --");
        
        if (repoC.listarTodos().isEmpty()) {
            System.out.println("Aviso: É necessário cadastrar pelo menos um Cliente (opção 4) antes de criar um pedido.");
            return;
        }
        
        if (repoF.listarTodos().isEmpty()) {
            System.out.println("Aviso: É necessário cadastrar pelo menos um Fornecedor (opção 5) antes de criar um pedido.");
            return;
        }
        

        try {
            System.out.print("ID do Pedido: ");
            Long idPedido = Long.parseLong(scanner.nextLine());

            //Seleção de Cliente
            System.out.println("\nClientes disponíveis:");
            for (Cliente c : repoC.listarTodos()) {
                System.out.println("ID: " + c.getId() + " | Nome: " + c.getNome());
            }
            System.out.print("Digite o ID do Cliente para o pedido: ");
            Long idCliente = Long.parseLong(scanner.nextLine());
            Cliente clienteSelecionado = repoC.buscarPorId(idCliente);
            if (clienteSelecionado == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            //Seleção de Fornecedor
            System.out.println("\nFornecedores disponíveis:");
            for (Fornecedor f : repoF.listarTodos()) {
                System.out.println("ID: " + f.getId() + " | Nome: " + f.getNome());
            }
            System.out.print("Digite o ID do Fornecedor para o pedido: ");
            Long idFornecedor = Long.parseLong(scanner.nextLine());
            Fornecedor fornecedorSelecionado = repoF.buscarPorId(idFornecedor);
            if (fornecedorSelecionado == null) {
                System.out.println("Fornecedor não encontrado.");
                return;
            }

            Pedido novoPedido = new Pedido(clienteSelecionado, fornecedorSelecionado, idPedido, "PENDENTE");

            //Exibe os produtos do fornecedor selecionado
            System.out.println("\n--- Produtos deste Fornecedor ---");
            boolean temProdutos = false;
            for (Produto p : controlProduto.listarTodos()) {
                if (p.getIdFornecedor() != null && p.getIdFornecedor().equals(fornecedorSelecionado.getId())) {
                    System.out.println("ID: " + p.getId() + " | " + p.getNome() + " | R$ " + p.getPreco() + " | " + p.getPeso() + "kg");
                    temProdutos = true;
                }
            }
            
            if (!temProdutos) {
                System.out.println("Aviso: Este fornecedor não tem produtos cadastrados. Operação cancelada.");
                return;
            }

            //Inserção de Itens Automatizada
            boolean adicionarMais = true;
            while (adicionarMais) {
                System.out.println("\n--- Adicionar Item ---");
                
                Produto prodEscolhido = null;
                while (prodEscolhido == null) {
                    try {
                        System.out.print("ID do Produto: ");
                        long idProd = Long.parseLong(scanner.nextLine());
                        
                        for (Produto p : controlProduto.listarTodos()) {
                            if (p.getId() == idProd && p.getIdFornecedor().equals(fornecedorSelecionado.getId())) {
                                prodEscolhido = p;
                                break;
                            }
                        }
                        if (prodEscolhido == null) {
                            System.out.println("Erro: Produto não encontrado ou não pertence a este fornecedor.");
                        }
                    } catch (Exception e) { System.out.println("Erro: Digite um ID numérico."); }
                }
               
                if (prodEscolhido.getPreco() == null) {
                    System.out.println("Erro: Este produto está com o preço inválido (nulo). Edite-o ou cadastre um novo.");
                    continue;
                }

                int qtd;
                while(true) {
                    try {
                        System.out.print("Quantidade: ");
                        qtd = Integer.parseInt(scanner.nextLine());
                        break;
                    } catch (Exception e) { 
                        System.out.println("Erro: Digite um número inteiro."); 
                    }
                }

                double pesoTotalItem = prodEscolhido.getPeso() * qtd;
                double precoUnitarioOriginal = prodEscolhido.getPreco();
                double precoFinal = precoUnitarioOriginal;
                Moeda moedaOrigem = fornecedorSelecionado.getMoedaPadrao();
                
                // 1. Descobre o símbolo da moeda do fornecedor
                String simboloFinal = "";
                if (moedaOrigem.name().equals("EUR")) simboloFinal = "€";
                else if (moedaOrigem.name().equals("GBP")) simboloFinal = "£";
                else if (moedaOrigem.name().equals("CNY")) simboloFinal = "¥";
                else if (moedaOrigem.name().equals("USD")) simboloFinal = "US$";
                else simboloFinal = "$";

                // 2. Tenta converter para Real
                try {
                    System.out.println("A consultar PTAX para " + moedaOrigem + "...");
                    beans.Cotacao cotacao = controlCambio.obterCotacao(moedaOrigem);
                    precoFinal = controlCambio.converterParaReal(precoUnitarioOriginal, cotacao);
                    
                    // Se não deu erro e converteu, muda o símbolo para Reais!
                    simboloFinal = "R$"; 
                    System.out.printf("Câmbio aplicado (1 %s = R$ %.4f)\n", moedaOrigem, cotacao.getVenda());
                } catch (Exception e) {
                    System.out.println("Aviso: Falha ao converter (" + e.getMessage() + "). O valor ficará em " + moedaOrigem + ".");
                }

                // 3. Adiciona o Item com o símbolo correto
                novoPedido.adicionar(new Item(prodEscolhido.getNome(), qtd, pesoTotalItem, precoFinal, simboloFinal));
                System.out.println("Item adicionado: " + qtd + "x " + prodEscolhido.getNome());

                System.out.print("\nDeseja adicionar mais um item? (S/N): ");
                String resp = scanner.nextLine().trim();
                
                if (!resp.equalsIgnoreCase("S")) {
                    adicionarMais = false; 
                }
            }

            //Salva na camada de negócio
            controlPedido.criarPedido(novoPedido);
            System.out.println("Pedido criado com sucesso!");

            //Gera o extrato em HTML
            try {
                controlPedido.exportarPedidoParaHtml(novoPedido.getId(), ".");
                System.out.println("Ficheiro 'Pedido_" + novoPedido.getId() + ".html' gerado na raiz do projeto!");
            } catch (Exception ex) {
                System.out.println("Aviso ao gerar HTML: " + ex.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao criar pedido: " + e.getMessage());
        }
    }
}