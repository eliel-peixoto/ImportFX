package App;

import beans.*;
import gui.*;
import negocio.*;
import dados.*;

public class App {
    public static void main(String[] args) throws Exception {
        Cliente c1 = new Cliente(11223344L, "Junior", 123456L, "Brasil");//cpf deve ser long [corrigir na classe]
        Cliente c2 = new Cliente(9192833L, "Alana", 67890L, "Alemanha");//cpf deve ser long [corrigir na classe]
        
        Fornecedor f1 = new Fornecedor();//colocar parametros
        Fornecedor f2 = new Fornecedor();//colocar parametros
        
        Eletronicos e1 = new Eletronicos();//colocar parametros
        Brinquedos b1 = new Brinquedos();//colocar parametros
        Vestuario v1 = new Vestuario();//colocar parametros
        
        //passar os produtos pra os Fornecedores !
        
        //criar 1 pedido com um fornecedor e um cliente !
        
    }
}
