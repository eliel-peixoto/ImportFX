package gui;

import beans.Vestuario;
import beans.Eletronico;
import beans.Brinquedo;
import beans.Produto;
import dados.RepositorioProdutos;
import negocio.GerenciadorProduto;

import java.util.Scanner;

public class MenuPrincipal {
    
    private GerenciadorProduto controlProduto;
    private Scanner scanner;

    public MenuPrincipal() {
        RepositorioProdutos repoProdutos = new RepositorioProdutos();
        this.controlProduto = new GerenciadorProduto(repoProdutos);
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
            System.out.println("4. Fazer uma Compra / Pedido (Em Breve)");
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
                        System.out.println("\nMódulo de Pedidos ainda não ligado à interface.");
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
}
