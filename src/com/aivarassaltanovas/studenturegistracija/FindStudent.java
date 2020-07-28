package com.aivarassaltanovas.studenturegistracija;

import com.aivarassaltanovas.studenturegistracija.gui.GUI;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.List;

import static com.aivarassaltanovas.studenturegistracija.MainWindow.ORANGE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.DARK_BLUE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.LIGHT_BLUE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.WHITE;


public class FindStudent implements GUI,
        PropertyChangeListener, ActionListener, ItemListener, KeyListener {

    private final String TITLE = "Studentų paieška";
    private final String[] COLUMN_NAMES = {"Nr.", "Vardas", "Pavarde", "Grupe"};
    private final String[] SAVE_OPTIONS = new String[]{"Eksportuoti", "Excel failą", "CSV failą"};

    private JPanel framePanel = new JPanel(new BorderLayout(10, 10));
    private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
    private JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
    private JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    private JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 9, 5));

    private JLabel title = new JLabel(TITLE, SwingConstants.CENTER);
    private JButton importData = new JButton("Įkelti studentus");
    private JComboBox<Object> saveOptions = new JComboBox<>(SAVE_OPTIONS);
    private JTextField search;

    private JTable table;
    public DefaultTableModel model;

    private Map<String, List<List<String>>> data;
    private JDateChooser dateChooser, dateChooser1;
    private MainWindow mainWindow;

    public FindStudent(Map<String, List<List<String>>> data, MainWindow mainWindow) {

        this.data = data;
        this.mainWindow = mainWindow;

        JFrame frame = new JFrame(TITLE);
        try {
            ImageIcon img = new ImageIcon(getClass().getResource("icon.png"));
            frame.setIconImage(img.getImage());
        } catch (NullPointerException e) {
            /*
             * if icon was not found
             */
        }

        designPanels();
        frame.add(framePanel, BorderLayout.CENTER);
        framePanel.add(titlePanel, BorderLayout.NORTH);
        framePanel.add(tablePanel, BorderLayout.CENTER);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        framePanel.add(buttonsPanel, BorderLayout.SOUTH);

        designLabels();
        titlePanel.add(title);

        search = createTextField("Ieškoti");
        search.addKeyListener(this);
        JLabel label = new JLabel("Įveskite paieškos žodį:");
        label.setForeground(WHITE);
        searchPanel.add(label);
        searchPanel.add(search, BorderLayout.CENTER);

        designComboBox(saveOptions);
        buttonsPanel.add(saveOptions);
        designButton(importData);
        buttonsPanel.add(importData, BorderLayout.NORTH);

        dateChooser = createDateChooser("NUO");
        dateChooser1 = createDateChooser("IKI");
        buttonsPanel.add(dateChooser);
        buttonsPanel.add(dateChooser1);

        table = createTable(COLUMN_NAMES);
        createScrollPane(tablePanel);
        addAllGroupStudents(data);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        framePanel.requestFocusInWindow();
    }

    public void addAllGroupStudents(Map<String, List<List<String>>> data) {

        int num = 1;
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(data.keySet().toArray()[i]).size(); j++) {
                model.addRow(new Object[]{num, data.get(data.keySet().toArray()[i]).get(j).get(0), data.get(data.keySet().toArray()[i]).get(j).get(1), data.keySet().toArray()[i]});
                num++;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {

        if (e.getPropertyName().equals("date") && dateChooser1.getDate() != null && dateChooser.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(dateChooser.getDate());
            String date1 = sdf.format(dateChooser1.getDate());

            Calendar c = Calendar.getInstance();
            Calendar c1 = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(date));
                c1.setTime(sdf.parse(date1));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            showChosenDates(c, c1, sdf);
        }
    }

    public void showChosenDates(Calendar c, Calendar c1, SimpleDateFormat sdf) {

        model.setColumnCount(4);
        while (c.before(c1) || c.equals(c1)) {
            String absent;
            int row = 0;
            model.addColumn(sdf.format(c.getTime()));

            for (int i = 0; i < data.size(); i++)
                for (int j = 0; j < data.get(data.keySet().toArray()[i]).size(); j++) {
                    if (data.get(data.keySet().toArray()[i]).get(j).contains(sdf.format(c.getTime()))) {
                        absent = "n";
                        model.setValueAt(absent, row, table.getColumnCount() - 1);
                    }
                    row++;
                }

            c.add(Calendar.DATE, 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(importData))
            new ImportDataFromExcel(table, model, data, mainWindow, this);
    }

    @Override
    public JTable createTable(String[] columnNames) {

        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        table.setModel(model);

        table.getTableHeader().setBackground(ORANGE);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(ORANGE));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(1).setMinWidth(130);
        table.getColumnModel().getColumn(2).setMinWidth(130);
        table.getColumnModel().getColumn(3).setMinWidth(115);
        return table;
    }

    @Override
    public void createScrollPane(JPanel tablePanel) {

        JScrollPane sp = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = LIGHT_BLUE;
                this.thumbDarkShadowColor = LIGHT_BLUE;
                this.trackColor = Color.white;
            }
        });

        sp.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = LIGHT_BLUE;
                this.thumbDarkShadowColor = LIGHT_BLUE;
                this.trackColor = Color.white;
            }
        });

        sp.setBorder(BorderFactory.createEmptyBorder());
        final JPanel corner = new JPanel();
        corner.setBackground(ORANGE);
        sp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, corner);
        tablePanel.add(sp);
    }

    @Override
    public void designPanels() {

        framePanel.setBackground(DARK_BLUE);
        titlePanel.setBackground(DARK_BLUE);
        tablePanel.setBackground(DARK_BLUE);
        searchPanel.setBackground(DARK_BLUE);
        buttonsPanel.setBackground(DARK_BLUE);

        framePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 12, 10));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 6, 0, ORANGE));
    }

    @Override
    public void designLabels() {
        title.setFont(new Font("Arial", Font.BOLD, 29));
        title.setForeground(WHITE);
    }

    @Override
    public JTextField createTextField(String text) {

        JTextField textField = new JTextField(15);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        textField.setForeground(Color.BLACK);
        textField.setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 1));
        textField.setName(text);
        return textField;
    }

    @Override
    public void designButton(JButton button) {

        button.setBackground(ORANGE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        button.addActionListener(this);
    }

    @Override
    public void designPopMenuItem(JMenuItem menuItem) {

    }

    @Override
    public void designComboBox(JComboBox<Object> comboBox) {

        comboBox.setBackground(ORANGE);
        comboBox.setFocusable(false);
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup basicComboPopup = new BasicComboPopup(comboBox);
                comboBox.setPreferredSize(new Dimension(150, 25));
                return basicComboPopup;
            }
        });
        comboBox.addItemListener(this);
    }

    private JDateChooser createDateChooser(String text) {

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(100, 25));
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setText(text);
        dateChooser.getDateEditor().addPropertyChangeListener(this);
        return dateChooser;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getItem() == SAVE_OPTIONS[1])
                new ExportDataFromTable(table, ".xls");

            else if (e.getItem() == SAVE_OPTIONS[2])
                new ExportDataFromTable(table, ".csv");

            System.out.println();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(rowSorter);
        String word = search.getText();

        if (word.length() == 0) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(RowFilter.regexFilter(word));
    }
}
