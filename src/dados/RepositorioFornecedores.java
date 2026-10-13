package dados;

import java.util.ArrayList;
import java.util.List;
import beans.Fornecedor;

public class RepositorioFornecedores implements IRepositorioFornecedores {
    private ArrayList<Fornecedor> listaFornecedor;

    public RepositorioFornecedores() {
        this.listaFornecedor = new ArrayList<>();
    }

    @Override
    public void adicionarFornecedor(Fornecedor f) {
        if (f != null && !listaFornecedor.contains(f)) {
            listaFornecedor.add(f);
        }
    }

    @Override
    public Fornecedor procurarFornecedor(int id, String nome) {
        for (Fornecedor f : listaFornecedor) {
            boolean idIgual = (id > 0 && f.getId() != null && f.getId() == id);
            boolean nomeIgual = (nome != null && !nome.isBlank() && f.getNome() != null && f.getNome().equalsIgnoreCase(nome.trim()));

            if (idIgual || nomeIgual) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void removerFornecedor(int id, String nome) {
        Fornecedor f = procurarFornecedor(id, nome);
        if (f != null) {
            listaFornecedor.remove(f);
        }
    }

    @Override
    public List<Fornecedor> listarTodos() {
        return new ArrayList<>(listaFornecedor);
    }
}