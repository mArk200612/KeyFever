import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONObject;


public class GamesManagerClient {
    private static final String BASE_URL = "https://keyfever.altervista.org/API";
    private JFrame frame;
    private JTextArea outputArea;
    private JTextField idField, titleField, categoryField, pegiField, priceField,
            imageField, youtubeField, developerField, yearField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryComboBox;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                GamesManagerClient window = new GamesManagerClient();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public GamesManagerClient() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Games Manager");
        frame.setBounds(100, 100, 900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Inizializzazione dei campi
        idField = new JTextField();
        titleField = new JTextField();

        // Creazione del JComboBox per le categorie
        String[] categories = {
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
        categoryComboBox = new JComboBox<>(categories);

        pegiField = new JTextField();
        descriptionArea = new JTextArea(5, 20);
        priceField = new JTextField();
        imageField = new JTextField();
        youtubeField = new JTextField();
        developerField = new JTextField();
        yearField = new JTextField();

        // Pannello input
        JPanel inputPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Game Details"));

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel("PEGI:"));
        inputPanel.add(pegiField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(new JScrollPane(descriptionArea));
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Image Path:"));
        inputPanel.add(imageField);
        inputPanel.add(new JLabel("YouTube Link:"));
        inputPanel.add(youtubeField);
        inputPanel.add(new JLabel("Developer:"));
        inputPanel.add(developerField);
        inputPanel.add(new JLabel("Release Year:"));
        inputPanel.add(yearField);

        // Pannello pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createBtn = new JButton("Create");
        JButton readBtn = new JButton("Read");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton listBtn = new JButton("List All");
        JButton clearBtn = new JButton("Clear");

        createBtn.addActionListener(e -> createGame());
        readBtn.addActionListener(e -> readGame());
        updateBtn.addActionListener(e -> updateGame());
        deleteBtn.addActionListener(e -> deleteGame());
        listBtn.addActionListener(e -> listGames());
        clearBtn.addActionListener(e -> clearFields());

        buttonPanel.add(createBtn);
        buttonPanel.add(readBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(listBtn);
        buttonPanel.add(clearBtn);

        // Area output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Aggiunta componenti al frame
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        frame.getContentPane().add(scrollPane, BorderLayout.SOUTH);
    }

    private void createGame() {
        try {
            JSONObject game = new JSONObject();
            game.put("titolo", titleField.getText());
            game.put("categoria", categoryComboBox.getSelectedItem());
            game.put("PEGI", Integer.parseInt(pegiField.getText()));
            game.put("descrizione", descriptionArea.getText());
            game.put("prezzo", Double.parseDouble(priceField.getText()));
            game.put("percorso_immagine", imageField.getText());
            game.put("youtube_link", youtubeField.getText());
            game.put("sviluppatore", developerField.getText());
            game.put("anno_uscita", yearField.getText());

            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/create").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = game.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject responseJson = new JSONObject(response.toString());
                idField.setText(responseJson.get("id_gioco").toString());
                outputArea.setText("Game created successfully! ID: " + responseJson.get("id_gioco"));
            } else {
                outputArea.setText("Error creating game: " + responseCode);
            }
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void readGame() {
        try {
            String id = idField.getText();
            if (id.isEmpty()) {
                outputArea.setText("Please enter an ID");
                return;
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/" + id).openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject game = new JSONObject(response.toString());
                displayGame(game);
                outputArea.setText("Game retrieved successfully!");
            } else {
                outputArea.setText("Game not found: " + responseCode);
            }
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void updateGame() {
        try {
            String id = idField.getText();
            if (id.isEmpty()) {
                outputArea.setText("Please enter an ID");
                return;
            }

            JSONObject game = new JSONObject();
            game.put("titolo", titleField.getText());
            game.put("categoria", categoryComboBox.getSelectedItem());
            game.put("PEGI", Integer.parseInt(pegiField.getText()));
            game.put("descrizione", descriptionArea.getText());
            game.put("prezzo", Double.parseDouble(priceField.getText()));
            game.put("percorso_immagine", imageField.getText());
            game.put("youtube_link", youtubeField.getText());
            game.put("sviluppatore", developerField.getText());
            game.put("anno_uscita", yearField.getText());

            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/update/" + id).openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = game.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                outputArea.setText("Game updated successfully!");
            } else {
                outputArea.setText("Error updating game: " + responseCode);
            }
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void deleteGame() {
        try {
            String id = idField.getText();
            if (id.isEmpty()) {
                outputArea.setText("Please enter an ID");
                return;
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/delete/" + id).openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                outputArea.setText("Game deleted successfully!");
                clearFields();
            } else {
                outputArea.setText("Error deleting game: " + responseCode);
            }
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            // Aggiungi parametri di filtro se necessario
            String url = BASE_URL;

            // Esempio con filtri (da implementare nella UI)
            // if (filterCategory != null) url += "?categoria=" + filterCategory;

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONArray games = new JSONArray(response.toString());
                outputArea.setText("Games List:\n");
                for (int i = 0; i < games.length(); i++) {
                    JSONObject game = games.getJSONObject(i);
                    outputArea.append(game.getInt("id_gioco") + ": " + game.getString("titolo") + "\n");
                }
            } else {
                outputArea.setText("Error listing games: " + responseCode);
            }
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void displayGame(JSONObject game) {
        titleField.setText(game.getString("titolo"));
        categoryComboBox.setSelectedItem(game.getString("categoria"));
        pegiField.setText(String.valueOf(game.getInt("PEGI")));
        descriptionArea.setText(game.getString("descrizione"));
        priceField.setText(String.valueOf(game.getDouble("prezzo")));
        imageField.setText(game.getString("percorso_immagine"));
        youtubeField.setText(game.getString("youtube_link"));
        developerField.setText(game.getString("sviluppatore"));
        yearField.setText(game.getString("anno_uscita"));
    }

    private void clearFields() {
        idField.setText("");
        titleField.setText("");
        categoryComboBox.setSelectedIndex(0);
        pegiField.setText("");
        descriptionArea.setText("");
        priceField.setText("");
        imageField.setText("");
        youtubeField.setText("");
        developerField.setText("");
        yearField.setText("");
    }
}