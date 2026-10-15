package app;
import beans.*;
//import gui.*;
//import negocio.*;
//import dados.*;

public class App {
    public static void main(String[] args) throws Exception {
        Cliente c1 = new Cliente(11223344L, "Junior", 123456L, "Brasil");//cpf deve ser long [corrigir na classe]
        Cliente c2 = new Cliente(9192833L, "Alana", 67890L, "Alemanha");//cpf deve ser long [corrigir na classe]

        Fornecedor f1 = new Fornecedor(1L, "TechCorp", "Japão", Moeda.USD);
        Fornecedor f2 = new Fornecedor(2L, "GlobalImport", "Alemanha", Moeda.EUR);
        
        Eletronico e1 = new Eletronico(1234L, "Placa-mãe", 1500.0, 150.0, 20.0);//colocar parametros
        Brinquedo b1 = new Brinquedo(5678L, "Carrinho", 50.0, 30.0, 10.0);//colocar parametros
        Vestuario v1 = new Vestuario(9876L, "Camisa personalizada", 50.0, 20.0, 12.0);//colocar parametros

        f1.getListaProdutos().adicionarProduto(e1);
        f1.getListaProdutos().adicionarProduto(b1);
        f2.getListaProdutos().adicionarProduto(v1);
        
        //criar 1 pedido com um fornecedor e um cliente !
        Pedido p1 = new Pedido(c1, f1, 0, null);
        Pedido p2 = new Pedido(c2, f2, 0, null);
    }
}
