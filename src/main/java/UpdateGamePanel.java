import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

public class UpdateGamePanel extends JPanel {
    private GamesManagerClient mainApp;
    private JTextField idField, titleField, pegiField, priceField, imageField, youtubeField, developerField, yearField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryComboBox;
    private JButton updateButton;

    public UpdateGamePanel(GamesManagerClient mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel at top
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Form panel in center
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Button panel at bottom
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Initially disable update button until a game is loaded
        updateButton.setEnabled(false);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Find Game to Update"));

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

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Edit Game Details"));

        // Initialize fields
        titleField = new JTextField();
        categoryComboBox = new JComboBox<>(GamesManagerClient.getGameCategories());
        pegiField = new JTextField();
        descriptionArea = new JTextArea(5, 20);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        priceField = new JTextField();
        imageField = new JTextField();
        youtubeField = new JTextField();
        developerField = new JTextField();
        yearField = new JTextField();

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

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        updateButton = new JButton("Update Game");
        updateButton.addActionListener(e -> updateGame());

        panel.add(updateButton);

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
                updateButton.setEnabled(true);
            } else {
                updateButton.setEnabled(false);
            }

        } catch (Exception ex) {
            mainApp.appendToOutput("Error finding game: " + ex.getMessage());
            ex.printStackTrace();
            updateButton.setEnabled(false);
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

    private void updateGame() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                mainApp.appendToOutput("Game ID is missing");
                return;
            }

            // Validate required fields
            if (titleField.getText().isEmpty()) {
                mainApp.appendToOutput("Title is a required field");
                return;
            }

            // Validate PEGI field
            String pegiText = pegiField.getText();
            if (pegiText.isEmpty()) {
                mainApp.appendToOutput("PEGI is a required field");
                return;
            }

            int pegiValue;
            try {
                pegiValue = Integer.parseInt(pegiText);
            } catch (NumberFormatException e) {
                mainApp.appendToOutput("PEGI must be a valid number");
                return;
            }

            // Create game object for update
            JSONObject game = new JSONObject();
            game.put("titolo", titleField.getText());
            game.put("categoria", categoryComboBox.getSelectedItem());
            game.put("PEGI", pegiValue);
            game.put("descrizione", descriptionArea.getText());

            // Handle price field
            String priceText = priceField.getText();
            double priceValue = 0.0;
            if (!priceText.isEmpty()) {
                try {
                    priceValue = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    mainApp.appendToOutput("Price must be a valid number");
                    return;
                }
            }
            game.put("prezzo", priceValue);

            game.put("percorso_immagine", imageField.getText());
            game.put("youtube_link", youtubeField.getText());
            game.put("sviluppatore", developerField.getText());
            game.put("anno_uscita", yearField.getText());

            // Send update request
            mainApp.clearOutput();
            JSONObject response = mainApp.updateGame(id, game);

            if (response.length() > 0) {
                // Update was successful
                mainApp.appendToOutputWithNewline("Game updated successfully!");
            }

        } catch (Exception ex) {
            mainApp.appendToOutput("Error updating game: " + ex.getMessage());
            ex.printStackTrace();
        }
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
        updateButton.setEnabled(false);
    }
}