package gui;

import beans.Vestuario;
import beans.Eletronico;
import beans.Brinquedo;
import beans.Produto;
import beans.Cliente;       
import beans.Fornecedor;
import beans.Moeda; // Importação da enumeração Moeda

import dados.RepositorioProdutos;
import dados.RepositorioCliente;    
import dados.RepositorioFornecedores; 

import negocio.GerenciadorProduto;
import negocio.GerenciadorCadastro;    

import java.util.Scanner;

public class MenuPrincipal {
    
    private GerenciadorProduto controlProduto;
    private GerenciadorCadastro controlCadastro;
    private Scanner scanner;

    public MenuPrincipal() {
        RepositorioProdutos repoProdutos = new RepositorioProdutos();
        this.controlProduto = new GerenciadorProduto(repoProdutos);
        
        RepositorioCliente repoC = new RepositorioCliente();
        RepositorioFornecedores repoF = new RepositorioFornecedores();
        this.controlCadastro = new GerenciadorCadastro(repoC, repoF);
        
        this.scanner = new Scanner(System.in);
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
            System.out.println("6. Fazer uma Compra / Pedido (Em Breve)");
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
                        System.out.println("\nAviso: Módulo de Pedidos ainda não ligado à interface.");
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
        System.out.println("Escolha a categoria do produto:");
        System.out.println("1 - Vestuário");
        System.out.println("2 - Eletrônico");
        System.out.println("3 - Brinquedo");
        System.out.print("Categoria escolhida: ");
        int tipoProduto = Integer.parseInt(scanner.nextLine());
        
        if (tipoProduto < 1 || tipoProduto > 3) {
            System.out.println("Categoria inválida. Retornando ao menu principal.");
            return;
        }

        System.out.print("ID do Produto: ");
        long id = Long.parseLong(scanner.nextLine());
        
        System.out.print("Nome do Produto: ");
        String nome = scanner.nextLine();
        
        System.out.print("Preço (Moeda Origem): ");
        Double preco = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Peso (kg): ");
        Double peso = Double.parseDouble(scanner.nextLine());
        
        Produto novoProduto = null;

        if (tipoProduto == 1) {
            novoProduto = new Vestuario(id, nome, preco, peso);
        } else if (tipoProduto == 2) {
            System.out.print("Alíquota (%): ");
            Double aliquota = Double.parseDouble(scanner.nextLine());
            novoProduto = new Eletronico(id, nome, preco, peso, aliquota);
        } else if (tipoProduto == 3) {
            System.out.print("Alíquota (%): ");
            Double aliquota = Double.parseDouble(scanner.nextLine());
            novoProduto = new Brinquedo(id, nome, preco, peso, aliquota);
        }
        
        if (novoProduto != null) {
            controlProduto.cadastrarProduto(novoProduto);
            System.out.println("Produto cadastrado com sucesso!");
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
        System.out.print("ID do Cliente: ");
        long id = Long.parseLong(scanner.nextLine());
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("CPF (Apenas números): ");
        Long cpf = Long.parseLong(scanner.nextLine()); 
        
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();
        
        Cliente novoCliente = new Cliente(id, nome, cpf, endereco);
        try {
            controlCadastro.cadastrarCliente(novoCliente);
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private void cadastrarFornecedorUI() {
        System.out.println("\n-- Cadastrar Fornecedor --");
        System.out.print("ID do Fornecedor: ");
        long id = Long.parseLong(scanner.nextLine());
        
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
}