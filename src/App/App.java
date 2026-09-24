package App;

import beans.*;

public class App {
    public static void main(String[] args) throws Exception {
        Cliente c1 = new Cliente("JJ", 123456, "asdfghjkl");
        Cliente c2 = new Cliente("AA", 67890, "qwertyuiop");
        
        Fornecedor f1 = new Fornecedor("Rock Lee", "Japão", "Yen");
        Fornecedor f2 = new Fornecedor("Zip Zlap", "Planeta Zinx", "Flux");
        
        CategoriaProduto cp1 = new CategoriaProduto(10, f1, "shuriken", 20, 10, 1);
        CategoriaProduto cp2 = new CategoriaProduto(20, f2, "arma lazer", 10, 100, 10000);
        
        f1.adicionar(cp1);
        f2.adicionar(cp2);
        
    }
}
