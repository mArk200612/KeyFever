import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

// TRADOTTO TUTTO
public class LeggiGiocoPannello extends JPanel {
    private ClientAPI app;
    private JTextField campoID, campoTitolo, campoPEGI, campoPrezzo, campoImmagine, campoYouTube, campoSviluppatore, campoAnno, campoDisponibilita;
    private JTextArea descrizione;
    private JComboBox<String> categorieComboBox;

    public LeggiGiocoPannello(ClientAPI app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pannelloDiRicerca = creaPannelloDiRicerca();
        add(pannelloDiRicerca, BorderLayout.NORTH);

        JPanel pannelloDettagli = creaPannelloDettagli();
        add(pannelloDettagli, BorderLayout.CENTER);
    }

    private JPanel creaPannelloDiRicerca() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pannello.setBorder(BorderFactory.createTitledBorder("Cerca gioco dall'ID"));

        JLabel idLabel = new JLabel("ID gioco: ");
        campoID = new JTextField(10);
        JButton bottoneDiRicerca = new JButton("Cerca");
        bottoneDiRicerca.addActionListener(e -> cercaGioco());

        JButton bottonePulisci = new JButton("Pulisci");
        bottonePulisci.addActionListener(e -> pulisci());

        pannello.add(idLabel);
        pannello.add(campoID);
        pannello.add(bottoneDiRicerca);
        pannello.add(bottonePulisci);

        return pannello;
    }

    private JPanel creaPannelloDettagli() {
        JPanel pannello = new JPanel(new GridLayout(9, 2, 10, 10));
        pannello.setBorder(BorderFactory.createTitledBorder("Dettagli del gioco"));

        campoTitolo = new JTextField();
        campoTitolo.setEditable(false);

        categorieComboBox = new JComboBox<>(ClientAPI.getCategorieGiochi());
        categorieComboBox.setEnabled(false);

        campoPEGI = new JTextField();
        campoPEGI.setEditable(false);

        descrizione = new JTextArea(5, 20);
        descrizione.setEditable(false);
        JScrollPane descScrollPane = new JScrollPane(descrizione);

        campoPrezzo = new JTextField();
        campoPrezzo.setEditable(false);

        campoImmagine = new JTextField();
        campoImmagine.setEditable(false);

        campoYouTube = new JTextField();
        campoYouTube.setEditable(false);

        campoSviluppatore = new JTextField();
        campoSviluppatore.setEditable(false);

        campoAnno = new JTextField();
        campoAnno.setEditable(false);

        campoDisponibilita = new JTextField();
        campoDisponibilita.setEditable(false);

        pannello.add(new JLabel("Titolo:"));
        pannello.add(campoTitolo);

        pannello.add(new JLabel("Categorie:"));
        pannello.add(categorieComboBox);

        pannello.add(new JLabel("PEGI:"));
        pannello.add(campoPEGI);

        pannello.add(new JLabel("Descrizione:"));
        pannello.add(descScrollPane);

        pannello.add(new JLabel("Prezzo:"));
        pannello.add(campoPrezzo);

        pannello.add(new JLabel("Percorso dell'immagine:"));
        pannello.add(campoImmagine);

        pannello.add(new JLabel("Link di YouTube:"));
        pannello.add(campoYouTube);

        pannello.add(new JLabel("Sviluppatore:"));
        pannello.add(campoSviluppatore);

        pannello.add(new JLabel("Anno di rilascio:"));
        pannello.add(campoAnno);

        pannello.add(new JLabel("Disponibilità:"));
        pannello.add(campoDisponibilita);

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
                mostraGioco(gioco);
            }

        } catch (Exception ex) {
            app.aggiungiOutput("Errore nella ricerca del gioco: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void mostraGioco(JSONObject game) {
        campoTitolo.setText(game.getString("titolo"));

        String categoria = game.optString("categoria", "");
        for (int i = 0; i < categorieComboBox.getItemCount(); i++) {
            if (categorieComboBox.getItemAt(i).equals(categoria)) {
                categorieComboBox.setSelectedIndex(i);
                break;
            }
        }

        campoPEGI.setText(String.valueOf(game.optInt("PEGI", 0)));
        descrizione.setText(game.optString("descrizione", ""));
        campoPrezzo.setText(String.valueOf(game.optDouble("prezzo", 0)));
        campoImmagine.setText(game.optString("percorso_immagine", ""));
        campoYouTube.setText(game.optString("youtube_link", ""));
        campoSviluppatore.setText(game.optString("sviluppatore", ""));
        campoAnno.setText(game.optString("anno_uscita", ""));
        campoDisponibilita.setText(game.optString("disponibilita", ""));

        descrizione.setCaretPosition(0);
    }

    private void pulisci() {
        campoID.setText("");
        campoTitolo.setText("");
        categorieComboBox.setSelectedIndex(0);
        campoPEGI.setText("");
        descrizione.setText("");
        campoPrezzo.setText("");
        campoImmagine.setText("");
        campoYouTube.setText("");
        campoSviluppatore.setText("");
        campoAnno.setText("");
        campoDisponibilita.setText("");
        app.pulisciOutput();
    }
}