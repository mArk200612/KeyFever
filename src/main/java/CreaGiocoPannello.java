import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

//TRADOTTO TUTTO

public class CreaGiocoPannello extends JPanel {
    private final ClientAPI app;
    private JTextField campoTitolo, campoPEGI, campoPrezzo, campoImmagine, campoYouTube, campoSviluppatore, campoAnno, campoDisponibilita;
    private JTextArea descrizione;
    private JComboBox<String> categorieComboBox;

    public CreaGiocoPannello(ClientAPI app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(ClientAPI.COLORE_SFONDO);

        JPanel formPanel = creaPannelloForm();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = creaPannelloBottone();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel creaPannelloForm() {
        JPanel pannello = new JPanel(new GridBagLayout());
        pannello.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ClientAPI.COLORE_PRINCIPALE, 1),
                "Crea un nuovo gioco",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                ClientAPI.FONT_HEADER,
                ClientAPI.COLORE_PRINCIPALE
        ));
        pannello.setBackground(Color.WHITE);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                pannello.getBorder()
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoTitolo = creaAreaDiTesto();
        categorieComboBox = creaComboBox();
        campoPEGI = creaAreaDiTesto();
        descrizione = createStyledTextArea();
        JScrollPane descScrollPane = new JScrollPane(descrizione);
        campoPrezzo = creaAreaDiTesto();
        campoImmagine = creaAreaDiTesto();
        campoYouTube = creaAreaDiTesto();
        campoSviluppatore = creaAreaDiTesto();
        campoAnno = creaAreaDiTesto();

        // Riga 0: Titolo
        gbc.gridx = 0;
        gbc.gridy = 0;
        pannello.add(new JLabel("Titolo:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(campoTitolo, gbc);

        // Riga 1: Categoria
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        pannello.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(categorieComboBox, gbc);

        // Riga 2: PEGI
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        pannello.add(new JLabel("PEGI:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(campoPEGI, gbc);

        // Riga 3: Descrizione
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        pannello.add(new JLabel("Descrizione:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(descScrollPane, gbc);

        // Riga 4: Prezzo
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        pannello.add(new JLabel("Prezzo:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(campoPrezzo, gbc);

        // Riga 5: Percorso Immagine
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        pannello.add(new JLabel("Percorso immagine:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(campoImmagine, gbc);

        // Riga 6: Link a YouTube
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        pannello.add(new JLabel("Link a YouTube:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(campoYouTube, gbc);

        // Riga 7: Sviluppatore
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0;
        pannello.add(new JLabel("Sviluppatore:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(campoSviluppatore, gbc);

        // Riga 8: Anno di rilascio
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0;
        pannello.add(new JLabel("Anno di rilascio:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pannello.add(campoAnno, gbc);

        // Riga 8: Disponibilità
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0;
        pannello.add(new JLabel("Disponibilità:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        campoDisponibilita = creaAreaDiTesto();
        pannello.add(campoDisponibilita, gbc);

        gbc.gridy = 9;
        gbc.weighty = 1.0;
        pannello.add(Box.createGlue(), gbc);

        return pannello;
    }

    private JPanel creaPannelloBottone() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pannello.setBackground(ClientAPI.COLORE_SFONDO);

        JButton bottoneCrea = app.creaBottone("Crea gioco");
        bottoneCrea.addActionListener(_ -> creaGioco());

        JButton bottonePulisci = app.creaBottone("Pulisci");
        bottonePulisci.setBackground(new Color(220, 220, 220));
        bottonePulisci.setForeground(ClientAPI.COLORE_TESTO);
        bottonePulisci.addActionListener(_ -> pulisci());

        pannello.add(bottoneCrea);
        pannello.add(bottonePulisci);

        return pannello;
    }

    private JTextField creaAreaDiTesto() {
        JTextField areaDiTesto = new JTextField();
        areaDiTesto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return areaDiTesto;
    }

    private JComboBox<String> creaComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(ClientAPI.getCategorieGiochi());
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valore, int indice,
                                                          boolean selezionato, boolean focussata) {
                super.getListCellRendererComponent(lista, valore, indice, selezionato, focussata);
                setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                return this;
            }
        });
        return comboBox;
    }

    private JTextArea createStyledTextArea() {
        JTextArea areaDiTesto = new JTextArea(5, 20);
        areaDiTesto.setLineWrap(true);
        areaDiTesto.setWrapStyleWord(true);
        areaDiTesto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return areaDiTesto;
    }

    private void creaGioco() {
        try {
            if (campoTitolo.getText().isEmpty() || campoPEGI.getText().isEmpty()) {
                app.aggiungiOutput("Titolo e PEGI sono campi obbligatori");
                return;
            }

            JSONObject gioco = new JSONObject();
            gioco.put("titolo", campoTitolo.getText());
            gioco.put("categoria", categorieComboBox.getSelectedItem());

            try {
                gioco.put("PEGI", Integer.parseInt(campoPEGI.getText()));
            } catch (NumberFormatException ex) {
                app.aggiungiOutput("PEGI deve essere un numero valido");
                return;
            }

            gioco.put("descrizione", descrizione.getText());

            if (!campoPrezzo.getText().isEmpty()) {
                try {
                    gioco.put("prezzo", Double.parseDouble(campoPrezzo.getText()));
                } catch (NumberFormatException ex) {
                    app.aggiungiOutput("Prezzo deve essere un numero valido");
                    return;
                }
            } else {
                gioco.put("prezzo", 0);
            }

            gioco.put("percorso_immagine", campoImmagine.getText());
            gioco.put("youtube_link", campoYouTube.getText());
            gioco.put("sviluppatore", campoSviluppatore.getText());
            gioco.put("anno_uscita", campoAnno.getText());
            gioco.put("disponibilita", campoDisponibilita.getText());

            app.pulisciOutput();
            JSONObject risposta = app.creaGioco(gioco);

            if (risposta.has("id_gioco")) {
                pulisci();
            }

        } catch (Exception ex) {
            app.aggiungiOutput("Errore nella creazione del gioco: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void pulisci() {
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