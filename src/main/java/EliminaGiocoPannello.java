import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

public class EliminaGiocoPannello extends JPanel {
    private API_CLIENT mainApp;
    private JTextField idField, titleField, categoryField, pegiField;
    private JButton deleteButton;

    public EliminaGiocoPannello(API_CLIENT mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Game preview panel
        JPanel previewPanel = createPreviewPanel();
        add(previewPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Initially disable delete button
        deleteButton.setEnabled(false);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Find Game to Delete"));

        JLabel idLabel = new JLabel("Game ID:");
        idField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> findGame());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());

        panel.add(idLabel);
        panel.add(idField);
        panel.add(searchButton);
        panel.add(clearButton);

        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Game Preview"));

        // Simple read-only fields to show key game info
        titleField = new JTextField();
        titleField.setEditable(false);

        categoryField = new JTextField();
        categoryField.setEditable(false);

        pegiField = new JTextField();
        pegiField.setEditable(false);

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("PEGI Rating:"));
        panel.add(pegiField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        deleteButton = new JButton("Delete Game");
        deleteButton.setBackground(new Color(255, 100, 100));
        deleteButton.addActionListener(e -> confirmDelete());

        panel.add(deleteButton);

        return panel;
    }

    private void findGame() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                mainApp.aggiungiOutput("Please enter a game ID");
                return;
            }

            mainApp.pulisciOutput();
            JSONObject game = mainApp.readGame(id);

            if (game.has("titolo")) {
                // Display preview of game
                titleField.setText(game.getString("titolo"));
                categoryField.setText(game.optString("categoria", ""));
                pegiField.setText(String.valueOf(game.optInt("PEGI", 0)));

                // Enable delete button
                deleteButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
            }

        } catch (Exception ex) {
            mainApp.aggiungiOutput("Error finding game: " + ex.getMessage());
            ex.printStackTrace();
            deleteButton.setEnabled(false);
        }
    }

    private void confirmDelete() {
        String id = idField.getText().trim();
        String title = titleField.getText();

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the game:\n" + title + " (ID: " + id + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            deleteGame();
        }
    }

    private void deleteGame() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                mainApp.aggiungiOutput("Game ID is missing");
                return;
            }

            mainApp.pulisciOutput();
            JSONObject response = mainApp.deleteGame(id);

            if (response.length() > 0) {
                // Delete was successful, clear the form
                clearForm();
            }

        } catch (Exception ex) {
            mainApp.aggiungiOutput("Error deleting game: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        categoryField.setText("");
        pegiField.setText("");
        mainApp.pulisciOutput();
        deleteButton.setEnabled(false);
    }
}