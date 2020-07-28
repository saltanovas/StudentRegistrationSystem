package com.aivarassaltanovas.studenturegistracija;

import com.aivarassaltanovas.studenturegistracija.gui.GUI;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;

import static com.aivarassaltanovas.studenturegistracija.MainWindow.ORANGE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.DARK_BLUE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.LIGHT_BLUE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.WHITE;

public class GroupList implements GUI,
        ActionListener, PropertyChangeListener, ListSelectionListener, TableModelListener, ItemListener {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDateTime now = LocalDateTime.now();
    private String nowDate = dtf.format(now);
    private final String[] COLUMN_NAMES = {"Nr.", "Vardas", "Pavarde", nowDate};

    public JFrame frame;

    private JPanel framePanel = new JPanel(new BorderLayout(10, 10));
    private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
    private JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));

    private JLabel title = new JLabel();

    private JButton attendance = new JButton("Suvesti lankomumą");
    private JButton remove = new JButton("Pašalinti");
    private JButton back = new JButton("Grįžti atgal");

    private final String[] items = new String[]{"Išsaugoti sąrašą PDF", "Su lankomumu", "Be lankomumo"};
    private JComboBox<Object> save = new JComboBox<>(items);

    private JTable table;
    public DefaultTableModel model;

    private JDateChooser dateChooser = new JDateChooser();

    private Map<String, List<List<String>>> data;
    public String groupName;

    GroupList(String groupName, Map<String, List<List<String>>> data) {

        this.data = data;
        this.groupName = groupName;

        frame = new JFrame(groupName + " studentų sąrašas");
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
        framePanel.add(buttonsPanel, BorderLayout.SOUTH);

        designLabels();
        titlePanel.add(title, BorderLayout.NORTH);

        designButton(attendance);
        designComboBox(save);
        designButton(remove);
        designDateChooser(dateChooser);
        designButton(back);
        dateChooser.setVisible(false);
        back.setVisible(false);
        buttonsPanel.add(attendance);
        buttonsPanel.add(save);
        buttonsPanel.add(remove);
        buttonsPanel.add(dateChooser);
        buttonsPanel.add(back);

        table = createTable(COLUMN_NAMES);
        createScrollPane(framePanel);

        attendance.addActionListener(this);
        remove.addActionListener(this);
        back.addActionListener(this);
        save.addItemListener(this);
        dateChooser.getDateEditor().addPropertyChangeListener(this);
        table.getModel().addTableModelListener(this);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(attendance)) {

            attendance.setVisible(false);
            save.setVisible(false);
            remove.setVisible(false);
            back.setVisible(true);
            dateChooser.setVisible(true);

            table.getColumnModel().getColumn(3).setMinWidth(100);
            table.getColumnModel().getColumn(3).setMaxWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);

        } else if (e.getSource().equals(remove)) {

            String Name = table.getValueAt(table.getSelectedRow(), 1).toString();
            String Surname = table.getValueAt(table.getSelectedRow(), 2).toString();

            if (table.getSelectedRow() != -1) {

                removeStudent(Name, Surname);
                addGroupStudents(groupName, data);
            }
        } else if (e.getSource().equals(back)) {
            attendance.setVisible(true);
            dateChooser.setVisible(false);
            save.setVisible(true);
            remove.setVisible(true);
            back.setVisible(false);

            table.getColumnModel().getColumn(3).setMinWidth(0);
            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setPreferredWidth(0);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("date")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            nowDate = sdf.format(dateChooser.getDate());
            table.getColumnModel().getColumn(3).setHeaderValue(nowDate);
            table.getTableHeader().repaint();
            addGroupStudents(groupName, data);
        }
    }

    public void removeAllRows() {
        for (int i = table.getRowCount() - 1; i >= 0; i--)
            model.removeRow(i);
    }

    public void removeStudent(String Name, String Surname) {

        removeAllRows();
        for (int i = 0; i < data.get(groupName).size(); i++) {
            if (data.get(groupName).get(i).get(0).equals(Name) && data.get(groupName).get(i).get(1).equals(Surname)) {
                data.get(groupName).get(i).clear();
                data.get(groupName).removeIf(x -> x != null && x.isEmpty()); // deletes empty list
                break;
            }
        }
    }

    public void addGroupStudents(String groupName, Map<String, List<List<String>>> data) {

        removeAllRows();
        boolean absent = false;
        if (data.containsKey(groupName)) {
            int num = 1;
            for (int j = 0; j < data.get(groupName).size(); j++) {
                if (data.get(groupName).get(j).contains(nowDate)) absent = true;
                model.addRow(new Object[]{num, data.get(groupName).get(j).get(0), data.get(groupName).get(j).get(1), absent});
                num++;
                absent = false;
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
    }

    @Override
    public void tableChanged(TableModelEvent e) {

        if (table.getSelectedRow() != -1 && e.getType() == TableModelEvent.UPDATE) {

            int row = table.getSelectedRow();
            int column = table.getSelectedColumn();
            String newStudent = model.getValueAt(row, column).toString();

            if (table.getSelectedColumn() != 3 && !isStudentInGroup(row, column)) {
                if (table.getSelectedColumn() == 1)
                    data.get(groupName).get(row).set(0, newStudent);

                else if (table.getSelectedColumn() == 2)
                    data.get(groupName).get(row).set(1, newStudent);

            } else {
                nowDate = table.getColumnModel().getColumn(3).getHeaderValue().toString();

                if (newStudent.equals("true"))
                    data.get(groupName).get(row).add(nowDate);
                if (newStudent.equals("false"))
                    data.get(groupName).get(row).remove(nowDate);
            }
            System.out.println(data);
        }
    }

    public boolean isStudentInGroup(int row, int column) {

        if (column == 1) {
            for (int i = 0; i < data.get(groupName).size(); i++) {
                if (data.get(groupName).get(i).get(0).equals(model.getValueAt(row, column).toString()) && data.get(groupName).get(i).get(1).equals(model.getValueAt(row, column + 1).toString()))
                    return true;
            }
        }

        if (column == 2) {
            for (int i = 0; i < data.get(groupName).size(); i++) {
                if (data.get(groupName).get(i).get(0).equals(model.getValueAt(row, column - 1).toString()) && data.get(groupName).get(i).get(1).equals(model.getValueAt(row, column).toString()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public JTable createTable(String[] columnNames) {

        JTable table = new JTable() {
            public boolean isCellEditable(int row, int column) {
                if (attendance.isVisible()) {
                    switch (column) {
                        case (1):
                        case (2):
                            return true;
                    }
                }
                return column == 3;
            }

            public Class getColumnClass(int column) {
                if (column == 3) return Boolean.class;
                else return String.class;
            }
        };
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(ORANGE);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(ORANGE));
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(COLUMN_NAMES);
        table.setModel(model);

        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(3).setPreferredWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(28);
        table.getColumnModel().getColumn(0).setMinWidth(28);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        return table;
    }

    @Override
    public void createScrollPane(JPanel tablePanel) {

        JScrollPane sp = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = LIGHT_BLUE;
                this.thumbDarkShadowColor = LIGHT_BLUE;
                this.trackColor = Color.WHITE;
            }
        });
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(sp.getPreferredSize().width, sp.getPreferredSize().height / 2));
        final JPanel corner = new JPanel();
        corner.setBackground(ORANGE);
        sp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, corner);
        tablePanel.add(sp);
    }

    @Override
    public void designPanels() {

        framePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 8, 10));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 6, 0, ORANGE));

        framePanel.setBackground(DARK_BLUE);
        titlePanel.setBackground(DARK_BLUE);
        buttonsPanel.setBackground(DARK_BLUE);
    }

    @Override
    public void designLabels() {

        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setText(groupName + " studentų sąrašas");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBackground(DARK_BLUE);
        title.setForeground(WHITE);
    }

    @Override
    public JTextField createTextField(String text) {
        return null;
    }

    @Override
    public void designButton(JButton button) {
        button.setBackground(ORANGE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
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
    }

    private void designDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(110, 25));
        dateChooser.setToolTipText(nowDate);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getItem() == items[1])
                new SaveToPDF(groupName, table, true);

            else if (e.getItem() == items[2])
                new SaveToPDF(groupName, table, false);
        }
    }
}
