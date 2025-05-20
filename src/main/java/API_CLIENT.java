import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class API_CLIENT {
    private static final String API = "https://keyfever.altervista.org/API";
    protected static final Color COLORE_PRINCIPALE = new Color(70, 130, 180);
    private static final Color COLORE_SECONDARIO = new Color(100, 149, 237);
    protected static final Color COLORE_SFONDO = new Color(240, 248, 255);
    protected static final Color COLORE_TESTO = new Color(50, 50, 50);
    protected static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BOTTONE = new Font("Segoe UI", Font.PLAIN, 12);

    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JTextArea output;

    private ListaGiochiPannello listaPannello;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
                API_CLIENT finestra = new API_CLIENT();
                finestra.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public API_CLIENT() {
        inizzializza();
    }

    private void inizzializza() {
        frame = new JFrame("Games Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLORE_SFONDO);

        output = new JTextArea(10, 50);
        output.setEditable(false);
        output.setFont(new Font("Consolas", Font.PLAIN, 12));
        output.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output Log"));

        JPanel navPanel = creaPannelloDiNavigazione();

        CreaGiocoPannello creaPannello = new CreaGiocoPannello(this);
        LeggiGiocoPannello leggiPannello = new LeggiGiocoPannello(this);
        AggiornaGiocoPannello aggiornaPannello = new AggiornaGiocoPannello(this);
        EliminaGiocoPannello eliminaPannello = new EliminaGiocoPannello(this);
        listaPannello = new ListaGiochiPannello(this);

        cardPanel.add(creaPannello, "create");
        cardPanel.add(leggiPannello, "read");
        cardPanel.add(aggiornaPannello, "update");
        cardPanel.add(eliminaPannello, "delete");
        cardPanel.add(listaPannello, "list");

        cardLayout.show(cardPanel, "create");

        frame.setLayout(new BorderLayout());
        frame.add(navPanel, BorderLayout.NORTH);
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
        EventQueue.invokeLater(() -> frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH));
    }

    private JPanel creaPannelloDiNavigazione() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(COLORE_PRINCIPALE);

        JButton bottoneCrea = creaBottone("Crea gioco");
        JButton bottoneLeggi = creaBottone("Trova gioco");
        JButton bottoneAggiorna = creaBottone("Aggiorna gioco");
        JButton bottoneElimina = creaBottone("Elimina gioco");
        JButton bottoneLista = creaBottone("Lista tutti i giochi");

        bottoneCrea.addActionListener(_ -> cardLayout.show(cardPanel, "crea"));
        bottoneLeggi.addActionListener(_ -> cardLayout.show(cardPanel, "leggi"));
        bottoneAggiorna.addActionListener(_ -> cardLayout.show(cardPanel, "aggiorna"));
        bottoneElimina.addActionListener(_ -> cardLayout.show(cardPanel, "elimina"));
        bottoneLista.addActionListener(_ -> {
            cardLayout.show(cardPanel, "lista");
            listaPannello.refreshListaGiochi();
        });

        navPanel.add(bottoneCrea);
        navPanel.add(bottoneLeggi);
        navPanel.add(bottoneAggiorna);
        navPanel.add(bottoneElimina);
        navPanel.add(bottoneLista);

        return navPanel;
    }

    protected JButton creaBottone(String text) {
        JButton bottone = new JButton(text);
        bottone.setFont(FONT_BOTTONE);
        bottone.setBackground(COLORE_SECONDARIO);
        bottone.setForeground(Color.BLACK);
        bottone.setFocusPainted(false);
        bottone.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bottone.setBackground(COLORE_PRINCIPALE.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bottone.setBackground(COLORE_SECONDARIO);
            }
        });

        return bottone;
    }

    // METODI PER CHIAMATE API - disponibili per tutti i pannelli

    public void aggiungiOutput(String text) {
        output.setText(text);
    }

    public void aggiungiOutputConACapo(String text) {
        output.append("\n" + text);
    }

    public void pulisciOutput() {
        output.setText("");
    }

    public JSONObject creaGioco(JSONObject datiGioco) throws Exception {
        HttpURLConnection connessione = (HttpURLConnection) new URI(API + "/create.php").toURL().openConnection();
        connessione.setRequestMethod("POST");
        connessione.setRequestProperty("Content-Type", "application/json");
        connessione.setDoOutput(true);

        try (OutputStream os = connessione.getOutputStream()) {
            byte[] input = datiGioco.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return gestisciRisposta(connessione, "creato", true);
    }

    public JSONObject readGame(String id) throws Exception {
        HttpURLConnection connsessione = (HttpURLConnection) new URI(API + "/read.php?id=" + id).toURL().openConnection();
        connsessione.setRequestMethod("GET");

        return gestisciRisposta(connsessione, "letto", false);
    }

    public JSONObject updateGame(String id, JSONObject datiGioco) throws Exception {
        HttpURLConnection connessione = (HttpURLConnection) new URI(API + "/update.php?id=" + id).toURL().openConnection();
        connessione.setRequestMethod("POST");
        connessione.setRequestProperty("Content-Type", "application/json");
        connessione.setDoOutput(true);

        try (OutputStream os = connessione.getOutputStream()) {
            byte[] input = datiGioco.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return gestisciRisposta(connessione, "aggiornato", false);
    }

    public JSONObject deleteGame(String id) throws Exception {
        HttpURLConnection connessione = (HttpURLConnection) new URI(API + "/delete.php?id=" + id).toURL().openConnection();
        connessione.setRequestMethod("POST");

        return gestisciRisposta(connessione, "cancellato", false);
    }

    public JSONArray listGames() throws Exception {
        HttpURLConnection connessione = (HttpURLConnection) new URI(API + "/read_all.php").toURL().openConnection();
        connessione.setRequestMethod("GET");

        int codiceRisposta = connessione.getResponseCode();
        if (codiceRisposta == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connessione.getInputStream()));
            String input;
            StringBuilder risposta = new StringBuilder();

            while ((input = in.readLine()) != null) {
                risposta.append(input);
            }
            in.close();

            String stringaRisposta = risposta.toString();
            aggiungiOutput("Risposta grezza: " + stringaRisposta);

            if (stringaRisposta.trim().startsWith("[")) {
                return new JSONArray(stringaRisposta);
            } else {
                aggiungiOutputConACapo("Attenzione: la risposta non è un array JSON valido.");
                return new JSONArray();
            }
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(connessione.getErrorStream()));
            String input;
            StringBuilder risposta = new StringBuilder();

            while ((input = in.readLine()) != null) {
                risposta.append(input);
            }
            in.close();

            aggiungiOutput("Errore nell'elencare i giochi (codice " + codiceRisposta + "): " + risposta);
            return new JSONArray();
        }
    }

    private JSONObject gestisciRisposta(HttpURLConnection connessione, String azione, boolean creato) throws Exception {
        int codiceRisposta = connessione.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                codiceRisposta < HttpURLConnection.HTTP_BAD_REQUEST ?
                        connessione.getInputStream() : connessione.getErrorStream()
        ));

        String input;
        StringBuilder risposta = new StringBuilder();
        while ((input = in.readLine()) != null) {
            risposta.append(input);
        }
        in.close();

        String stringaRisposta = risposta.toString();
        aggiungiOutput("Risposta grezza: " + stringaRisposta);

        if (codiceRisposta == HttpURLConnection.HTTP_OK ||
                (creato && codiceRisposta == HttpURLConnection.HTTP_CREATED)) {
            if (stringaRisposta.trim().startsWith("{")) {
                JSONObject responseJson = new JSONObject(stringaRisposta);
                aggiungiOutputConACapo("\nGioco " + azione + " con successo!");
                return responseJson;
            } else {
                aggiungiOutputConACapo("\nAttenzione: la risposta non è un oggetto JSON valido: " + stringaRisposta);
                return new JSONObject();
            }
        } else {
            if (stringaRisposta.trim().startsWith("{")) {
                JSONObject errorJson = new JSONObject(stringaRisposta);
                aggiungiOutputConACapo("\nErrore: " + errorJson.optString("messaggio", "Errore sconosciuto"));
            } else {
                aggiungiOutputConACapo("\nErrore: " + stringaRisposta);
            }
            return new JSONObject();
        }
    }

    public static String[] getCategorieGiochi() {
        return new String[] {
                "ActionAdventure", "Simulation", "Strategy", "Sports", "Racing",
                "Puzzle", "Idle", "Educational", "Party", "Music", "Fighting",
                "Shooter", "Platformer", "Stealth", "Survival", "Horror",
                "Sandbox", "FirstPersonShooter", "ThirdPersonShooter", "MMORPG",
                "MOBA", "RealTimeStrategy", "TurnBasedStrategy", "JRPG",
                "ActionRPG", "ClassicRPG", "RacingSimulation", "ArcadeRacing",
                "BattleRoyale", "Roguelike", "Roguelite", "Metroidvania",
                "GrandStrategy", "CardGame", "BoardGame", "TextAdventure",
                "PointAndClick", "VisualNovel", "ManagementSim", "LifeSim",
                "SocialSim", "SurvivalHorror", "PsychologicalHorror",
                "ImmersiveSim", "DatingSim", "Gacha", "TowerDefense",
                "Rhythm", "AutoBattler", "WalkingSimulator", "Soulslike",
                "OpenWorld", "CozyGame", "InteractiveMovie", "InteractiveFiction",
                "VirtualReality", "AugmentedReality", "CryptoGaming",
                "BattlePassGames", "HyperCasual", "RealTimeTactics",
                "Wargame", "BulletHell", "Clicker", "Trivia", "EscapeRoom"
        };
    }
}