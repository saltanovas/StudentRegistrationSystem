package com.aivarassaltanovas.studenturegistracija;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class ExportDataFromTable extends UserNotifications {

    public ExportDataFromTable(JTable table, String fileType) {

        JFileChooser jfc = new JFileChooser();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(jfc);

        int option = jfc.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = jfc.getSelectedFile().getName();
            String path = jfc.getSelectedFile().getParentFile().getPath();

            int len = filename.length();
            String ext = "";
            String file;

            if (len > 4) ext = filename.substring(len - 4, len);

            if (fileType.equals(".xls")) {
                if (ext.equals(".xls")) file = path + "\\" + filename;
                else file = path + "\\" + filename + ".xls";
                export(table, new File(file), '\t');
            } else {
                if (ext.equals(".csv")) file = path + "\\" + filename;
                else file = path + "\\" + filename + ".csv";
                export(table, new File(file), ',');
            }
        }

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(jfc);
    }

    private void export(JTable table, File file, char c) {

        try {
            FileWriter excel = new FileWriter(file);

            if(c == ',') {
                excel.write("sep=,");
                excel.write("\n");
            }

            for (int i = 0; i < table.getColumnCount(); i++) {
                excel.write(table.getColumnName(i));
                excel.write(c);
            }

            excel.write("\n");

            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    if (null == table.getValueAt(i, j)) excel.write("" + c);
                    else if (table.getValueAt(i, j).equals(true)) excel.write("n" + c);
                    else excel.write(table.getValueAt(i, j).toString() + c);
                }
                excel.write("\n");
            }

            excel.close();
        } catch (IOException e) {
            fileException("Failas tokiu vardu nerastas!");
        }
    }
}