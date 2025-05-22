import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

// TRADOTTO TUTTO

public class EliminaGiocoPannello extends JPanel {
    private final ClientAPI app;
    private JTextField campoID, campoTitolo, campoCategoria, campoPEGI, campoDisponibilita;
    private JButton bottoneElimina;

    public EliminaGiocoPannello(ClientAPI app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pannelloDiRicerca = creaPannelloDiRicerca();
        add(pannelloDiRicerca, BorderLayout.NORTH);

        JPanel pannelloAnteprima = creaPannelloAnteprima();
        add(pannelloAnteprima, BorderLayout.CENTER);

        JPanel pannelloBottone = creaPannelloBottone();
        add(pannelloBottone, BorderLayout.SOUTH);

        bottoneElimina.setEnabled(false);
    }

    private JPanel creaPannelloDiRicerca() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pannello.setBorder(BorderFactory.createTitledBorder("Cerca un gioco da eliminare"));

        JLabel idLabel = new JLabel("ID del gioco::");
        campoID = new JTextField(10);
        JButton bottoneCerca = new JButton("Cerca");
        bottoneCerca.addActionListener(_ -> cercaGioco());

        JButton bottonePulisci = new JButton("Pulisci");
        bottonePulisci.addActionListener(_ -> pulisci());

        pannello.add(idLabel);
        pannello.add(campoID);
        pannello.add(bottoneCerca);
        pannello.add(bottonePulisci);

        return pannello;
    }

    private JPanel creaPannelloAnteprima() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Anteprima del gioco"));

        campoTitolo = new JTextField();
        campoTitolo.setEditable(false);

        campoCategoria = new JTextField();
        campoCategoria.setEditable(false);

        campoPEGI = new JTextField();
        campoPEGI.setEditable(false);

        campoDisponibilita = new JTextField();
        campoDisponibilita.setEditable(false);


        panel.add(new JLabel("Titolo:"));
        panel.add(campoTitolo);
        panel.add(new JLabel("Categoria:"));
        panel.add(campoCategoria);
        panel.add(new JLabel("PEGI:"));
        panel.add(campoPEGI);
        panel.add(new JLabel("Disponibilità:"));
        panel.add(campoDisponibilita);

        return panel;
    }

    private JPanel creaPannelloBottone() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        bottoneElimina = new JButton("Delete Game");
        bottoneElimina.setBackground(new Color(255, 100, 100));
        bottoneElimina.addActionListener(_ -> confermaEliminazione());

        pannello.add(bottoneElimina);

        return pannello;
    }

    private void cercaGioco() {
        try {
            String id = campoID.getText().trim();
            if (id.isEmpty()) {
                app.aggiungiOutput("Perfavore inserisci un ID");
                return;
            }

            app.pulisciOutput();
            JSONObject gioco = app.cercaGioco(id);

            if (gioco.has("titolo")) {
                campoTitolo.setText(gioco.getString("titolo"));
                campoCategoria.setText(gioco.optString("categoria", ""));
                campoPEGI.setText(String.valueOf(gioco.optInt("PEGI", 0)));
                campoDisponibilita.setText(gioco.optString("disponibilita", ""));

                bottoneElimina.setEnabled(true);
            } else {
                bottoneElimina.setEnabled(false);
            }

        } catch (Exception ex) {
            app.aggiungiOutput("Errore nella ricerca del gioco: " + ex.getMessage());
            ex.printStackTrace();
            bottoneElimina.setEnabled(false);
        }
    }

    private void confermaEliminazione() {
        String id = campoID.getText().trim();
        String titolo = campoTitolo.getText();

        int risultato = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler eliminare questo gioco?\n" + titolo + " (ID: " + id + ")?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (risultato == JOptionPane.YES_OPTION) {
            eliminaGioco();
        }
    }

    private void eliminaGioco() {
        try {
            String id = campoID.getText().trim();
            if (id.isEmpty()) {
                app.aggiungiOutput("ID del gioco mancante");
                return;
            }

            app.pulisciOutput();
            JSONObject risposta = app.deleteGame(id);

            if (!risposta.isEmpty()) {
                pulisci();
            }

        } catch (Exception ex) {
            app.aggiungiOutput("Errore nell'eliminazione del gioco: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void pulisci() {
        campoID.setText("");
        campoTitolo.setText("");
        campoCategoria.setText("");
        campoPEGI.setText("");
        campoDisponibilita.setText("");
        app.pulisciOutput();
        bottoneElimina.setEnabled(false);
    }
}