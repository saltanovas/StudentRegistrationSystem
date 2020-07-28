package com.aivarassaltanovas.studenturegistracija;

import com.aivarassaltanovas.studenturegistracija.gui.GUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class MainWindow extends UserNotifications implements GUI,
        ActionListener, FocusListener, KeyListener, MouseListener, EventListener, TableModelListener {

    public static final Color DARK_BLUE = new Color(80, 90, 108);
    public static final Color ORANGE = new Color(236, 164, 118);
    public static final Color LIGHT_BLUE = new Color(163, 184, 204);
    public static final Color WHITE = new Color(223, 232, 240);

    private final String TITLE = "Studentų registracijos sistema";
    private final String[] COLUMN_NAMES = {"Nr.", "Grupės pavadinimas"};

    private JPanel framePanel = new JPanel(new BorderLayout(20, 0));
    private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
    private JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
    private JPanel inputPanel = new JPanel(new GridBagLayout());
    private JPanel tablePanel = new JPanel(new BorderLayout(0, 10));

    private JLabel title = new JLabel(TITLE, SwingConstants.CENTER);
    private JLabel message = new JLabel(" ");

    private JButton save = new JButton("Įrašyti");
    private JButton find = new JButton("Studento paieška");

    private JTextField name, surname, group; //created automatically, because all text fields are the same

    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem delete = new JMenuItem("Pašalinti grupę");
    private JMenuItem edit = new JMenuItem("Keisti grupės pavadinimą");

    private JTable table;
    private DefaultTableModel model;
    boolean tableEditable = false;

    private GroupList groupList;
    private FindStudent findStudent;

    private Map<String, List<List<String>>> data = new LinkedHashMap<>();

    public MainWindow() {

        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
            frame.setIconImage(icon.getImage());
        } catch (NullPointerException e) {
            /*
             * if icon was not found
             */
        }

        designPanels();
        frame.add(framePanel, BorderLayout.CENTER);
        framePanel.add(titlePanel, BorderLayout.NORTH);
        framePanel.add(messagePanel, BorderLayout.SOUTH);
        framePanel.add(inputPanel, BorderLayout.WEST);
        framePanel.add(tablePanel, BorderLayout.EAST);

        designLabels();
        titlePanel.add(title, BorderLayout.CENTER);
        messagePanel.add(message, BorderLayout.CENTER);

        name = createTextField("Vardas");
        surname = createTextField("Pavardė");
        group = createTextField("Grupė");
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 7, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(name, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(surname, gbc);
        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(group, gbc);

        designButton(save);
        designButton(find);
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(save, gbc);
        tablePanel.add(find, BorderLayout.SOUTH);

        designPopMenuItem(edit);
        designPopMenuItem(delete);
        popupMenu.add(delete);
        popupMenu.add(edit);

        table = createTable(COLUMN_NAMES);
        createScrollPane(tablePanel);

        name.addFocusListener(this);
        surname.addFocusListener(this);
        group.addFocusListener(this);
        name.addKeyListener(this);
        surname.addKeyListener(this);

        save.addActionListener(this);
        find.addActionListener(this);
        delete.addActionListener(this);
        edit.addActionListener(this);

        table.addMouseListener(this);
        table.getModel().addTableModelListener(this);
        table.addMouseListener(this);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        framePanel.requestFocusInWindow();
        frame.getRootPane().setDefaultButton(save);
    }

    public boolean isStudentInGroup(String Name, String Surname, String Group) {

        if (data.containsKey(Group)) {
            for (int i = 0; i < data.get(Group).size(); i++) {
                if (data.get(Group).get(i).get(0).equals(Name) && data.get(Group).get(i).get(1).equals(Surname))
                    return true;
            }
        }
        return false;
    }

    public void representGroupInTable(String Name, String Surname, String Group) {

        boolean createNewGroup = true;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Group.equals(model.getValueAt(i, 1))) {
                createNewGroup = false;
                List<String> student = new ArrayList<>();
                student.add(Name);
                student.add(Surname);
                data.get(Group).add(student);
            }
        }

        if (createNewGroup) {
            List<List<String>> students = new ArrayList<>();
            List<String> student = new ArrayList<>();
            student.add(Name);
            student.add(Surname);
            students.add(student);
            data.put(Group, students);
            model.addRow(new Object[]{table.getRowCount() + 1, Group});
        }
    }

    private void fillAllGroupsInTable(Map<String, List<List<String>>> duomenys) {

        for (int i = table.getRowCount() - 1; i >= 0; i--)
            model.removeRow(i);

        for (int i = 0; i < duomenys.size(); i++)
            model.addRow(new Object[]{i + 1, duomenys.keySet().toArray()[i]});
    }

    private boolean areTextFieldsEmpty() {
        return name.getText().isEmpty() || name.getText().equals(name.getName())
                || surname.getText().isEmpty() || surname.getText().equals(surname.getName())
                || group.getText().isEmpty() || group.getText().equals(group.getName());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(save)) {
            if (!areTextFieldsEmpty()) {
                deleteMessage(message);
                String Name = name.getText().trim();
                String Surname = surname.getText().trim();
                String Group = group.getText().trim();

                if (!isStudentInGroup(Name, Surname, Group)) {
                    successfulInput(message, Name, Surname, Group);
                    representGroupInTable(Name, Surname, Group);

                    if (groupList != null && groupList.groupName.equals(Group))
                        groupList.model.addRow(new Object[]{groupList.model.getRowCount() + 1, Name, Surname});

                    if (findStudent != null)
                        findStudent.model.addRow(new Object[]{findStudent.model.getRowCount() + 1, Name, Surname, Group});

                } else studentAlreadyExist(message, Group);

            } else emptyInput(message);

        } else if (e.getSource().equals(find) && (groupList == null || !groupList.frame.isVisible()))
            findStudent = new FindStudent(data, this);

        else if (e.getSource().equals(delete) && table.getSelectedRow() != -1)
            deleteGroup(data);

        else if (e.getSource().equals(edit))
            table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
    }

    private void deleteGroup(Map<String, List<List<String>>> data) {
        String key = table.getValueAt(table.getSelectedRow(), 1).toString();
        data.remove(key);
        fillAllGroupsInTable(data);
        successfulRemove(message, key);
    }

    @Override
    public void focusGained(FocusEvent e) {

        if (e.getSource().equals(name) && name.getText().equals(name.getName())) {
            name.setText("");
            name.setForeground(Color.BLACK);
        } else if (e.getSource().equals(surname) && surname.getText().equals(surname.getName())) {
            surname.setText("");
            surname.setForeground(Color.BLACK);
        } else if (e.getSource().equals(group) && group.getText().equals(group.getName())) {
            group.setText("");
            group.setForeground(Color.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {

        if (e.getSource().equals(name) && name.getText().isEmpty()) {
            name.setText(name.getName());
            name.setForeground(Color.LIGHT_GRAY);
        } else if (e.getSource().equals(surname) && surname.getText().isEmpty()) {
            surname.setText(surname.getName());
            surname.setForeground(Color.LIGHT_GRAY);
        } else if (e.getSource().equals(group) && group.getText().isEmpty()) {
            group.setText(group.getName());
            group.setForeground(Color.LIGHT_GRAY);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

        if (!(Character.isLetter(e.getKeyChar())))
            e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if ((groupList == null || !groupList.frame.isVisible()) && table.getSelectedRow() >= 0 && e.getClickCount() == 2) {

            String groupName = table.getValueAt(table.getSelectedRow(), 1).toString();
            groupList = new GroupList(groupName, data);
            groupList.addGroupStudents(groupName, data);
            groupList.frame.setVisible(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
            tableEditable = true;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void tableChanged(TableModelEvent e) {

        tableEditable = false;
        if (table.getSelectedRow() != -1 && e.getType() == TableModelEvent.UPDATE) {

            int row = table.getSelectedRow();
            int column = table.getSelectedColumn();
            String newKey = model.getValueAt(row, column).toString();
            Object oldKey = data.keySet().toArray()[row];

            if (!data.containsKey(newKey) && !newKey.equals("")) {

                List<List<String>> newList = data.remove(oldKey);
                data.put(newKey, newList);
                successfulEdit(message, oldKey.toString(), newKey);

            } else if (newKey.equals("")) emptyGroup(message);
            else groupAlreadyExist(message, newKey);

            fillAllGroupsInTable(data);
        }
    }

    @Override
    public JTable createTable(String[] COLUMN_NAMES) {

        JTable table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return tableEditable;
            }
        };
        model = new DefaultTableModel();
        model.setColumnIdentifiers(COLUMN_NAMES);
        table.setModel(model);

        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(ORANGE);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(ORANGE));
        table.getColumnModel().getColumn(0).setMaxWidth(28);
        table.getColumnModel().getColumn(0).setMinWidth(28);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
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
                this.trackColor = Color.WHITE;
            }
        });
        sp.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = LIGHT_BLUE;
                this.thumbDarkShadowColor = LIGHT_BLUE;
                this.trackColor = Color.WHITE;
            }
        });
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(sp.getPreferredSize().width / 2, sp.getPreferredSize().height / 2));
        final JPanel corner = new JPanel();
        corner.setBackground(ORANGE);
        sp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, corner);
        tablePanel.add(sp);
    }

    @Override
    public void designPanels() {

        framePanel.setBackground(DARK_BLUE);
        titlePanel.setBackground(DARK_BLUE);
        messagePanel.setBackground(Color.WHITE);
        inputPanel.setBackground(DARK_BLUE);
        tablePanel.setBackground(DARK_BLUE);

        framePanel.setBorder(BorderFactory.createEmptyBorder(5, 8, 8, 8));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 6, 0, ORANGE));
        messagePanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(6, 0, 0, 0, ORANGE),
                new TitledBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.white), "Pranešimų skydelis")));
        tablePanel.setBorder(new TitledBorder(BorderFactory.createMatteBorder(20, 2, 10, 0, DARK_BLUE), "Įrašytos grupės"));
    }

    @Override
    public void designLabels() {

        title.setFont(new Font("Arial", Font.BOLD, 29));
        message.setFont(new Font("Arial", Font.BOLD, 15));
        title.setForeground(WHITE);
        message.setForeground(Color.RED);
    }

    @Override
    public JTextField createTextField(String text) {

        JTextField textField = new JTextField(text, 15);
        textField.setName(text);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        textField.setForeground(Color.LIGHT_GRAY);
        textField.setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 1));
        return textField;
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

        menuItem.setBackground(DARK_BLUE);
        menuItem.setForeground(WHITE);
    }

    @Override
    public void designComboBox(JComboBox<Object> comboBox) {

    }
}
