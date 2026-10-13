package App;

import beans.*;
import gui.*;
import negocio.*;
import dados.*;

public class App {
    public static void main(String[] args) throws Exception {
        Cliente c1 = new Cliente(11223344L, "Junior", 123456L, "Brasil");//cpf deve ser long [corrigir na classe]
        Cliente c2 = new Cliente(9192833L, "Alana", 67890L, "Alemanha");//cpf deve ser long [corrigir na classe]

        Fornecedor f1 = new Fornecedor(1L, "TechCorp", "Japão", Moeda.USD);
        Fornecedor f2 = new Fornecedor(2L, "GlobalImport", "Alemanha", Moeda.EUR);
        
        Eletronicos e1 = new Eletronicos();//colocar parametros
        Brinquedos b1 = new Brinquedos();//colocar parametros
        Vestuario v1 = new Vestuario();//colocar parametros

        f1.getListaProdutos().adicionarProduto(e1);
        f1.getListaProdutos().adicionarProduto(b1);
        f2.getListaProdutos().adicionarProduto(v1);
        
        //criar 1 pedido com um fornecedor e um cliente !
        
    }
}
