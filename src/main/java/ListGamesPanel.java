import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class ListGamesPanel extends JPanel {
    private GamesManagerClient mainApp;
    private JTable gamesTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ListGamesPanel(GamesManagerClient mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(GamesManagerClient.BACKGROUND_COLOR);

        // Table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GamesManagerClient.PRIMARY_COLOR, 1),
                "Games List",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                GamesManagerClient.HEADER_FONT,
                GamesManagerClient.PRIMARY_COLOR
        ));
        panel.setBackground(Color.WHITE);

        // Create table model with column names
        String[] columnNames = {"ID", "Title", "Category", "PEGI", "Price", "Developer", "Year"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table with custom renderer
        gamesTable = new JTable(tableModel);
        gamesTable.setSelectionBackground(Color.WHITE);
        gamesTable.setSelectionForeground(Color.BLACK);
        gamesTable.setFocusable(false);

        // Customize table appearance
        gamesTable.setFillsViewportHeight(true);
        gamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gamesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        gamesTable.setRowHeight(25);
        gamesTable.setShowGrid(false);
        gamesTable.setIntercellSpacing(new Dimension(0, 0));
        gamesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Custom header
        JTableHeader header = gamesTable.getTableHeader();
        header.setFont(GamesManagerClient.HEADER_FONT);
        header.setBackground(GamesManagerClient.PRIMARY_COLOR);
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);

        // Set column widths
        TableColumnModel columnModel = gamesTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);    // ID
        columnModel.getColumn(1).setPreferredWidth(200);   // Title
        columnModel.getColumn(2).setPreferredWidth(120);   // Category
        columnModel.getColumn(3).setPreferredWidth(50);    // PEGI
        columnModel.getColumn(4).setPreferredWidth(70);    // Price
        columnModel.getColumn(5).setPreferredWidth(150);   // Developer
        columnModel.getColumn(6).setPreferredWidth(70);    // Year

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(gamesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(GamesManagerClient.BACKGROUND_COLOR);

        refreshButton = mainApp.createStyledButton("Refresh List");
        refreshButton.addActionListener(e -> refreshGamesList());

        panel.add(refreshButton);

        return panel;
    }

    public void refreshGamesList() {
        try {
            // Clear existing table data
            tableModel.setRowCount(0);

            // Get games list
            mainApp.clearOutput();
            JSONArray games = mainApp.listGames();

            // Add each game to the table
            for (int i = 0; i < games.length(); i++) {
                JSONObject game = games.getJSONObject(i);

                Object[] rowData = {
                        (Object) game.getInt("id_gioco"),
                        game.getString("titolo"),
                        game.optString("categoria", ""),
                        (Object) game.optInt("PEGI", 0),
                        (Object) game.optDouble("prezzo", 0.0),
                        game.optString("sviluppatore", ""),
                        game.optString("anno_uscita", "")
                };

                tableModel.addRow(rowData);
            }

            mainApp.appendToOutputWithNewline("Total games: " + games.length());

        } catch (Exception ex) {
            mainApp.appendToOutput("Error loading games list: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}