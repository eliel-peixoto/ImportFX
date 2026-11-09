package gui;

import dados.Repositorios;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import negocio.GerenciadorRelatorios;
import negocio.IGerenciadorRelatorios;

/** Abre a tela de relatórios em uma janela JavaFX. */
public final class JanelaRelatorio {
    private JanelaRelatorio() { }

    public static void abrir(Window proprietaria, IGerenciadorRelatorios gerenciador) throws Exception {
        FXMLLoader loader = new FXMLLoader(JanelaRelatorio.class.getResource("/gui/TelaRelatorio.fxml"));
        Parent raiz = loader.load();
        TelaRelatorioController controller = loader.getController();
        controller.setGerenciadorRelatorios(gerenciador == null
                ? new GerenciadorRelatorios(Repositorios.pedidos()) : gerenciador);

        Stage janela = new Stage();
        if (proprietaria != null) janela.initOwner(proprietaria);
        janela.setTitle("ImportFX - Relatórios CSV");
        janela.setMinWidth(900);
        janela.setMinHeight(650);
        janela.setScene(new Scene(raiz));
        janela.show();
    }
}
