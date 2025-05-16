import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class GamesManagerClient {
    // Constants
    private static final String BASE_URL = "https://keyfever.altervista.org/API";
    protected static final Color PRIMARY_COLOR = new Color(70, 130, 180); // SteelBlue
    private static final Color SECONDARY_COLOR = new Color(100, 149, 237); // CornflowerBlue
    protected static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // AliceBlue
    protected static final Color TEXT_COLOR = new Color(50, 50, 50);
    protected static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Main Frame and Card Layout
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Shared components
    private JTextArea outputArea;

    // Pages
    private CreateGamePanel createPanel;
    private ReadGamePanel readPanel;
    private UpdateGamePanel updatePanel;
    private DeleteGamePanel deletePanel;
    private ListGamesPanel listPanel;

    public static void main(String[] args) {
        try {
            // Set system look and feel for better appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create card layout for switching between panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BACKGROUND_COLOR);

        // Create output area (shared between all panels)
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Output Log"));

        // Create navigation panel
        JPanel navPanel = createNavigationPanel();

        // Create all page panels
        createPanel = new CreateGamePanel(this);
        readPanel = new ReadGamePanel(this);
        updatePanel = new UpdateGamePanel(this);
        deletePanel = new DeleteGamePanel(this);
        listPanel = new ListGamesPanel(this);

        // Add panels to card layout
        cardPanel.add(createPanel, "create");
        cardPanel.add(readPanel, "read");
        cardPanel.add(updatePanel, "update");
        cardPanel.add(deletePanel, "delete");
        cardPanel.add(listPanel, "list");

        // Set initial panel
        cardLayout.show(cardPanel, "create");

        // Set up main layout
        frame.setLayout(new BorderLayout());
        frame.add(navPanel, BorderLayout.NORTH);
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(outputScrollPane, BorderLayout.SOUTH);

        // Ensure the frame is truly maximized
        frame.setVisible(true);
        EventQueue.invokeLater(() -> {
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        });
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(PRIMARY_COLOR);

        JButton createBtn = createStyledButton("Create Game");
        JButton readBtn = createStyledButton("Find Game");
        JButton updateBtn = createStyledButton("Update Game");
        JButton deleteBtn = createStyledButton("Delete Game");
        JButton listBtn = createStyledButton("List All Games");

        createBtn.addActionListener(e -> cardLayout.show(cardPanel, "create"));
        readBtn.addActionListener(e -> cardLayout.show(cardPanel, "read"));
        updateBtn.addActionListener(e -> cardLayout.show(cardPanel, "update"));
        deleteBtn.addActionListener(e -> cardLayout.show(cardPanel, "delete"));
        listBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "list");
            listPanel.refreshGamesList();
        });

        navPanel.add(createBtn);
        navPanel.add(readBtn);
        navPanel.add(updateBtn);
        navPanel.add(deleteBtn);
        navPanel.add(listBtn);

        return navPanel;
    }

    protected JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
        });

        return button;
    }

    // Methods for API calls - available to all panels

    public void appendToOutput(String text) {
        outputArea.setText(text);
    }

    public void appendToOutputWithNewline(String text) {
        outputArea.append("\n" + text);
    }

    public void clearOutput() {
        outputArea.setText("");
    }

    public JSONObject createGame(JSONObject gameData) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/create.php").openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = gameData.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return handleResponse(conn, "created", true);
    }

    public JSONObject readGame(String id) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/read.php?id=" + id).openConnection();
        conn.setRequestMethod("GET");

        return handleResponse(conn, "retrieved", false);
    }

    public JSONObject updateGame(String id, JSONObject gameData) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/update.php?id=" + id).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = gameData.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return handleResponse(conn, "updated", false);
    }

    public JSONObject deleteGame(String id) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/delete.php?id=" + id).openConnection();
        conn.setRequestMethod("POST");

        return handleResponse(conn, "deleted", false);
    }

    public JSONArray listGames() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/read_all.php").openConnection();
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

            String responseStr = response.toString();
            appendToOutput("Raw response: " + responseStr);

            if (responseStr.trim().startsWith("[")) {
                return new JSONArray(responseStr);
            } else {
                appendToOutputWithNewline("Warning: Response is not a valid JSON array");
                return new JSONArray();
            }
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            appendToOutput("Error listing games (code " + responseCode + "): " + response.toString());
            return new JSONArray();
        }
    }

    private JSONObject handleResponse(HttpURLConnection conn, String action, boolean isCreate) throws Exception {
        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                responseCode < HttpURLConnection.HTTP_BAD_REQUEST ?
                        conn.getInputStream() : conn.getErrorStream()
        ));

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String responseStr = response.toString();
        appendToOutput("Raw response: " + responseStr);

        if (responseCode == HttpURLConnection.HTTP_OK ||
                (isCreate && responseCode == HttpURLConnection.HTTP_CREATED)) {
            if (responseStr.trim().startsWith("{")) {
                JSONObject responseJson = new JSONObject(responseStr);
                appendToOutputWithNewline("\nGame " + action + " successfully!");
                return responseJson;
            } else {
                appendToOutputWithNewline("\nWarning: Response is not a valid JSON object: " + responseStr);
                return new JSONObject();
            }
        } else {
            if (responseStr.trim().startsWith("{")) {
                JSONObject errorJson = new JSONObject(responseStr);
                appendToOutputWithNewline("\nError: " + errorJson.optString("message", "Unknown error"));
                return new JSONObject();
            } else {
                appendToOutputWithNewline("\nError: " + responseStr);
                return new JSONObject();
            }
        }
    }

    // Utility method for category list - can be used by all panels
    public static String[] getGameCategories() {
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