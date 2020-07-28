package com.aivarassaltanovas.studenturegistracija;

import jxl.read.biff.BiffException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ImportDataFromExcel extends UserNotifications {

    public ImportDataFromExcel(JTable table, DefaultTableModel model, Map<String, List<List<String>>> data, MainWindow mainWindow, FindStudent findStudnet) {

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

            if (len > 4)
                ext = filename.substring(len - 4, len);

            if (ext.equals(".xls"))
                file = path + "\\" + filename;
            else
                file = path + "\\" + filename + ".xls";
            importData(table, model, data, mainWindow, findStudnet, new File(file));
        }

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(jfc);
    }


    public void importData(JTable table, DefaultTableModel model, Map<String, List<List<String>>> data, MainWindow mainWindow, FindStudent findStudent, File file) {

        try {
            jxl.Workbook workbook = jxl.Workbook.getWorkbook(file);
            jxl.Sheet sheet = workbook.getSheet(0);
            for (int j = 1; j < sheet.getRows(); j++) {
                String Name = sheet.getCell(1, j).getContents();
                String Surname = sheet.getCell(2, j).getContents();
                String Group = sheet.getCell(3, j).getContents();
                if (!mainWindow.isStudentInGroup(Name, Surname, Group)) {
                    mainWindow.representGroupInTable(Name, Surname, Group);
                }

                for (int i = 4; i < sheet.getColumns(); i++) {
                    String absentOrNot = sheet.getCell(i, j).getContents();
                    String newDate = sheet.getCell(i, 0).getContents();
                    deleteDateOrNot(data, Name, Surname, Group, newDate, absentOrNot);

                }
                new Vector().add("\n");
            }

            for (int i = table.getRowCount() - 1; i >= 0; i--)
                model.removeRow(i);

            findStudent.addAllGroupStudents(data);

        } catch (ArrayIndexOutOfBoundsException e) {
            fileException("Faile turi būti bent keturi stulpeliai!");
        } catch (IOException e) {
            fileException("Failas tokiu vardu nerastas!");
        } catch(BiffException e){
            fileException("Failo neįmanoma atidaryti!");
        }
    }


    public void deleteDateOrNot(Map<String, List<List<String>>> data, String Name, String Surname, String Group, String newDate, String absentOrNot) {

        for (int y = 0; y < data.get(Group).size(); y++) {

            if (data.get(Group).get(y).get(0).equals(Name) && data.get(Group).get(y).get(1).equals(Surname)) {

                if (data.get(Group).get(y).contains(newDate) && absentOrNot.equals(""))
                    data.get(Group).get(y).remove(newDate);
                else if (!data.get(Group).get(y).contains(newDate) && absentOrNot.equals("n"))
                    data.get(Group).get(y).add(newDate);
            }
        }
    }
}