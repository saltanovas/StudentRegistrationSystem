package com.aivarassaltanovas.studenturegistracija.gui;

import javax.swing.*;

public interface GUI {
    JTable createTable(String[] columnNames);
    void createScrollPane(JPanel tablePanel);
    void designPanels();
    void designLabels();
    JTextField createTextField(String text);
    void designButton(JButton button);
    void designPopMenuItem(JMenuItem menuItem);
    void designComboBox(JComboBox<Object> comboBox);
}

