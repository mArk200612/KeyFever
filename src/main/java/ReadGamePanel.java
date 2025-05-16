import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

public class ReadGamePanel extends JPanel {
    private GamesManagerClient mainApp;
    private JTextField idField, titleField, pegiField, priceField, imageField, youtubeField, developerField, yearField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryComboBox;

    public ReadGamePanel(GamesManagerClient mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel at top
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Game details panel
        JPanel detailsPanel = createDetailsPanel();
        add(detailsPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Find Game by ID"));

        JLabel idLabel = new JLabel("Game ID:");
        idField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> findGame());

        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());

        panel.add(idLabel);
        panel.add(idField);
        panel.add(searchButton);
        panel.add(clearButton);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Game Details"));

        // Initialize fields (all read-only)
        titleField = new JTextField();
        titleField.setEditable(false);

        categoryComboBox = new JComboBox<>(GamesManagerClient.getGameCategories());
        categoryComboBox.setEnabled(false);

        pegiField = new JTextField();
        pegiField.setEditable(false);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setEditable(false);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        priceField = new JTextField();
        priceField.setEditable(false);

        imageField = new JTextField();
        imageField.setEditable(false);

        youtubeField = new JTextField();
        youtubeField.setEditable(false);

        developerField = new JTextField();
        developerField.setEditable(false);

        yearField = new JTextField();
        yearField.setEditable(false);

        // Add components to panel
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel("PEGI Rating:"));
        panel.add(pegiField);
        panel.add(new JLabel("Description:"));
        panel.add(descScrollPane);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Image Path:"));
        panel.add(imageField);
        panel.add(new JLabel("YouTube Link:"));
        panel.add(youtubeField);
        panel.add(new JLabel("Developer:"));
        panel.add(developerField);
        panel.add(new JLabel("Release Year:"));
        panel.add(yearField);

        return panel;
    }

    private void findGame() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                mainApp.appendToOutput("Please enter a game ID");
                return;
            }

            mainApp.clearOutput();
            JSONObject game = mainApp.readGame(id);

            if (game.has("titolo")) {
                displayGame(game);
            }

        } catch (Exception ex) {
            mainApp.appendToOutput("Error finding game: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void displayGame(JSONObject game) {
        titleField.setText(game.getString("titolo"));

        // Set category
        String category = game.optString("categoria", "");
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            if (categoryComboBox.getItemAt(i).equals(category)) {
                categoryComboBox.setSelectedIndex(i);
                break;
            }
        }

        pegiField.setText(String.valueOf(game.optInt("PEGI", 0)));
        descriptionArea.setText(game.optString("descrizione", ""));
        priceField.setText(String.valueOf(game.optDouble("prezzo", 0)));
        imageField.setText(game.optString("percorso_immagine", ""));
        youtubeField.setText(game.optString("youtube_link", ""));
        developerField.setText(game.optString("sviluppatore", ""));
        yearField.setText(game.optString("anno_uscita", ""));

        // Scroll description to top
        descriptionArea.setCaretPosition(0);
    }

    private void clearForm() {
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
        mainApp.clearOutput();
    }
}