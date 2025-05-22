import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

// TRADOTTO TUTTO

public class AggiornaGiocoPannello extends JPanel {
    private final ClientAPI app;
    private JTextField campoID, campoTitolo, campoPEGI, campoPrezzo, campoImmagine, campoYouTube, campoSviluppatore, campoAnno, campoDisponibilita;
    private JTextArea descrizione;
    private JComboBox<String> categorieComboBox;
    private JButton bottoneAggiorna;

    public AggiornaGiocoPannello(ClientAPI app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pannelloDiRicerca = creaPannelloDiRicerca();
        add(pannelloDiRicerca, BorderLayout.NORTH);

        JPanel pannelloForm = creaPannelloForm();
        add(pannelloForm, BorderLayout.CENTER);

        JPanel pannelloBottone = creaPannelloBottone();
        add(pannelloBottone, BorderLayout.SOUTH);

        bottoneAggiorna.setEnabled(false);
    }

    private JPanel creaPannelloDiRicerca() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pannello.setBorder(BorderFactory.createTitledBorder("Cerca un gioco da aggiornare"));

        JLabel idLabel = new JLabel("ID del gioco:");
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

    private JPanel creaPannelloForm() {
        JPanel pannello = new JPanel(new GridLayout(9, 2, 10, 10));
        pannello.setBorder(BorderFactory.createTitledBorder("Modifica i dettagli del gioco"));

        campoTitolo = new JTextField();
        categorieComboBox = new JComboBox<>(ClientAPI.getCategorieGiochi());
        campoPEGI = new JTextField();
        descrizione = new JTextArea(5, 20);
        JScrollPane descScrollPane = new JScrollPane(descrizione);
        campoPrezzo = new JTextField();
        campoImmagine = new JTextField();
        campoYouTube = new JTextField();
        campoSviluppatore = new JTextField();
        campoAnno = new JTextField();
        campoDisponibilita = new JTextField();

        pannello.add(new JLabel("Titolo:"));
        pannello.add(campoTitolo);

        pannello.add(new JLabel("Categoria:"));
        pannello.add(categorieComboBox);

        pannello.add(new JLabel("PEGI:"));
        pannello.add(campoPEGI);

        pannello.add(new JLabel("Descrizione:"));
        pannello.add(descScrollPane);

        pannello.add(new JLabel("Prezzo:"));
        pannello.add(campoPrezzo);

        pannello.add(new JLabel("Percorso immagine:"));
        pannello.add(campoImmagine);

        pannello.add(new JLabel("Link a YouTube:"));
        pannello.add(campoYouTube);

        pannello.add(new JLabel("Sviluppatore:"));
        pannello.add(campoSviluppatore);

        pannello.add(new JLabel("Anno di rilascio:"));
        pannello.add(campoAnno);

        pannello.add(new JLabel("Disponibilità:"));
        pannello.add(campoDisponibilita);

        return pannello;
    }

    private JPanel creaPannelloBottone() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        bottoneAggiorna = new JButton("Update Game");
        bottoneAggiorna.addActionListener(_ -> aggiornaGioco());

        pannello.add(bottoneAggiorna);

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
                bottoneAggiorna.setEnabled(true);
            } else {
                bottoneAggiorna.setEnabled(false);
            }

        } catch (Exception ex) {
            app.aggiungiOutput("Errore nella ricerca del gioco: " + ex.getMessage());
            ex.printStackTrace();
            bottoneAggiorna.setEnabled(false);
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

    private void aggiornaGioco() {
        try {
            String id = campoID.getText().trim();
            if (id.isEmpty()) {
                app.aggiungiOutput("ID del gioco mancante");
                return;
            }

            if (campoTitolo.getText().isEmpty()) {
                app.aggiungiOutput("Il titolo è un campo obbligatorio");
                return;
            }

            String pegiText = campoPEGI.getText();
            if (pegiText.isEmpty()) {
                app.aggiungiOutput("La PEGI è un campo obbligatorio");
                return;
            }

            int valorePEGI;
            try {
                valorePEGI = Integer.parseInt(pegiText);
            } catch (NumberFormatException e) {
                app.aggiungiOutput("La PEGI deve essere un numero valido");
                return;
            }

            JSONObject gioco = new JSONObject();
            gioco.put("titolo", campoTitolo.getText());
            gioco.put("categoria", categorieComboBox.getSelectedItem());
            gioco.put("PEGI", valorePEGI);
            gioco.put("descrizione", descrizione.getText());

            String testoPrezzo = campoPrezzo.getText();
            double valorePrezzo = 0.0;
            if (!testoPrezzo.isEmpty()) {
                try {
                    valorePrezzo = Double.parseDouble(testoPrezzo);
                } catch (NumberFormatException e) {
                    app.aggiungiOutput("Il prezzo deve essere un numero valido");
                    return;
                }
            }
            gioco.put("prezzo", valorePrezzo);

            gioco.put("percorso_immagine", campoImmagine.getText());
            gioco.put("youtube_link", campoYouTube.getText());
            gioco.put("sviluppatore", campoSviluppatore.getText());
            gioco.put("anno_uscita", campoAnno.getText());
            gioco.put("disponibilita", campoDisponibilita.getText());


            app.pulisciOutput();
            JSONObject risposta = app.updateGame(id, gioco);

            if (!risposta.isEmpty()) {
                app.aggiungiOutputConACapo("Gioco aggiornato con successo!");
            }

        } catch (Exception ex) {
            app.aggiungiOutput("Errore nell'aggiornamento del gioco: " + ex.getMessage());
            ex.printStackTrace();
        }
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
        bottoneAggiorna.setEnabled(false);
    }
}