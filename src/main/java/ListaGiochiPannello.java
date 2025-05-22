import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class ListaGiochiPannello extends JPanel {
    private final ClientAPI app;
    private DefaultTableModel modelloTabella;

    public ListaGiochiPannello(ClientAPI app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(ClientAPI.COLORE_SFONDO);

        JPanel pannelloTabella = creaPannelloTabella();
        add(pannelloTabella, BorderLayout.CENTER);

        JPanel pannelloBottone = creaPannelloBottone();
        add(pannelloBottone, BorderLayout.SOUTH);
    }

    private JPanel creaPannelloTabella() {
        JPanel pannello = new JPanel(new BorderLayout());
        pannello.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ClientAPI.COLORE_PRINCIPALE, 1),
                "Lista dei giochi",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                ClientAPI.FONT_HEADER,
                ClientAPI.COLORE_PRINCIPALE
        ));
        pannello.setBackground(Color.WHITE);

        String[] nomiColonne = {"ID", "Titolo", "Categoria", "PEGI", "Prezzo", "Sviluppatore", "Anno", "Disponibilità"};
        modelloTabella = new DefaultTableModel(nomiColonne, 0) {
            @Override
            public boolean isCellEditable(int riga, int colonna) {
                return false;
            }
        };

        JTable tabellaGiochi = getTabellaGiochi();

        JTableHeader header = tabellaGiochi.getTableHeader();
        header.setFont(ClientAPI.FONT_HEADER);
        header.setBackground(ClientAPI.COLORE_PRINCIPALE);
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);

        TableColumnModel modelloColonna = tabellaGiochi.getColumnModel();
        modelloColonna.getColumn(0).setPreferredWidth(50);
        modelloColonna.getColumn(1).setPreferredWidth(200);
        modelloColonna.getColumn(2).setPreferredWidth(120);
        modelloColonna.getColumn(3).setPreferredWidth(50);
        modelloColonna.getColumn(4).setPreferredWidth(70);
        modelloColonna.getColumn(5).setPreferredWidth(150);
        modelloColonna.getColumn(6).setPreferredWidth(70);
        modelloColonna.getColumn(7).setPreferredWidth(100);


        JScrollPane scrollPane = new JScrollPane(tabellaGiochi);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pannello.add(scrollPane, BorderLayout.CENTER);

        return pannello;
    }

    private JTable getTabellaGiochi() {
        JTable tabellaGiochi = new JTable(modelloTabella);
        tabellaGiochi.setSelectionBackground(Color.WHITE);
        tabellaGiochi.setSelectionForeground(Color.BLACK);
        tabellaGiochi.setFocusable(false);

        tabellaGiochi.setFillsViewportHeight(true);
        tabellaGiochi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaGiochi.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabellaGiochi.setRowHeight(25);
        tabellaGiochi.setShowGrid(false);
        tabellaGiochi.setIntercellSpacing(new Dimension(0, 0));
        tabellaGiochi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return tabellaGiochi;
    }

    private JPanel creaPannelloBottone() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pannello.setBackground(ClientAPI.COLORE_SFONDO);

        JButton bottoneAggiorna = app.creaBottone("Aggiorna lista");
        bottoneAggiorna.addActionListener(_ -> aggiornaListaGiochi());

        pannello.add(bottoneAggiorna);

        return pannello;
    }

    public void aggiornaListaGiochi() {
        try {
            modelloTabella.setRowCount(0);

            app.pulisciOutput();
            JSONArray giochi = app.listGames();

            for (int i = 0; i < giochi.length(); i++) {
                JSONObject gioco = giochi.getJSONObject(i);

                Object[] datiRiga = {
                        gioco.getInt("id_gioco"),
                        gioco.getString("titolo"),
                        gioco.optString("categoria", ""),
                        gioco.optInt("PEGI", 0),
                        gioco.optDouble("prezzo", 0.0),
                        gioco.optString("sviluppatore", ""),
                        gioco.optString("anno_uscita", ""),
                        gioco.optString("disponibilita", "")

                };

                modelloTabella.addRow(datiRiga);
            }

            app.aggiungiOutputConACapo("Totale giochi: " + giochi.length());

        } catch (Exception ex) {
            app.aggiungiOutput("Errore nel caricamento della lista dei giochi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}