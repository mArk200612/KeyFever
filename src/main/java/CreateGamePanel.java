import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

public class CreateGamePanel extends JPanel {
    private GamesManagerClient mainApp;
    private JTextField titleField, pegiField, priceField, imageField, youtubeField, developerField, yearField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryComboBox;

    public CreateGamePanel(GamesManagerClient mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(GamesManagerClient.BACKGROUND_COLOR);

        // Form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GamesManagerClient.PRIMARY_COLOR, 1),
                "Create New Game",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                GamesManagerClient.HEADER_FONT,
                GamesManagerClient.PRIMARY_COLOR
        ));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                panel.getBorder()
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize fields
        titleField = createStyledTextField();
        categoryComboBox = createStyledComboBox();
        pegiField = createStyledTextField();
        descriptionArea = createStyledTextArea();
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        priceField = createStyledTextField();
        imageField = createStyledTextField();
        youtubeField = createStyledTextField();
        developerField = createStyledTextField();
        yearField = createStyledTextField();

        // Row 0: Title
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(titleField, gbc);

        // Row 1: Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(categoryComboBox, gbc);

        // Row 2: PEGI
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("PEGI Rating:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(pegiField, gbc);

        // Row 3: Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(descScrollPane, gbc);

        // Row 4: Price
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(priceField, gbc);

        // Row 5: Image Path
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Image Path:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(imageField, gbc);

        // Row 6: YouTube Link
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("YouTube Link:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(youtubeField, gbc);

        // Row 7: Developer
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        panel.add(new JLabel("Developer:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(developerField, gbc);

        // Row 8: Release Year
        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0;
        panel.add(new JLabel("Release Year:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(yearField, gbc);

        // Add padding at bottom
        gbc.gridy = 9;
        gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(GamesManagerClient.BACKGROUND_COLOR);

        JButton createButton = mainApp.createStyledButton("Create Game");
        createButton.addActionListener(e -> createGame());

        JButton clearButton = mainApp.createStyledButton("Clear Form");
        clearButton.setBackground(new Color(220, 220, 220));
        clearButton.setForeground(GamesManagerClient.TEXT_COLOR);
        clearButton.addActionListener(e -> clearForm());

        panel.add(createButton);
        panel.add(clearButton);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>(GamesManagerClient.getGameCategories());
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                return this;
            }
        });
        return combo;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(5, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return area;
    }

    private void createGame() {
        try {
            // Validate required fields
            if (titleField.getText().isEmpty() || pegiField.getText().isEmpty()) {
                mainApp.appendToOutput("Title and PEGI are required fields");
                return;
            }

            // Create game object
            JSONObject game = new JSONObject();
            game.put("titolo", titleField.getText());
            game.put("categoria", categoryComboBox.getSelectedItem());

            // Validate and parse PEGI
            try {
                game.put("PEGI", Integer.parseInt(pegiField.getText()));
            } catch (NumberFormatException ex) {
                mainApp.appendToOutput("PEGI must be a valid number");
                return;
            }

            game.put("descrizione", descriptionArea.getText());

            // Validate and parse price
            if (!priceField.getText().isEmpty()) {
                try {
                    game.put("prezzo", Double.parseDouble(priceField.getText()));
                } catch (NumberFormatException ex) {
                    mainApp.appendToOutput("Price must be a valid number");
                    return;
                }
            } else {
                game.put("prezzo", 0);
            }

            game.put("percorso_immagine", imageField.getText());
            game.put("youtube_link", youtubeField.getText());
            game.put("sviluppatore", developerField.getText());
            game.put("anno_uscita", yearField.getText());

            // Send create request
            mainApp.clearOutput();
            JSONObject response = mainApp.createGame(game);

            // Clear form on success
            if (response.has("id_gioco")) {
                clearForm();
            }

        } catch (Exception ex) {
            mainApp.appendToOutput("Error creating game: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
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