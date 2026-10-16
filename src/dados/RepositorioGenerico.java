package dados;

import java.util.ArrayList;
import java.util.List;

public abstract class RepositorioGenerico<T> {   
    protected List<T> elementos;

    public RepositorioGenerico() {
        this.elementos = new ArrayList<>();
    }

    public void cadastrar(T elemento) {
        this.elementos.add(elemento);
    }

    public List<T> listarTodos() {
        return this.elementos;
    }

    public void removerElemento(T elemento) {
        this.elementos.remove(elemento);
    }
}