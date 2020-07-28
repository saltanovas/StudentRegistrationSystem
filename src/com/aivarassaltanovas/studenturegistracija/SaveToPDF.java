package com.aivarassaltanovas.studenturegistracija;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.*;

public class SaveToPDF extends UserNotifications {

    private JTable table;
    private String groupName;
    private Document document;
    private boolean withAttendance;

    public SaveToPDF(String groupName, JTable table, boolean withAttendance) {

        this.groupName = groupName;
        this.table = table;
        this.withAttendance = withAttendance;

        try {
            if (table.getRowCount() > 0) {
                PdfPTable pdfTable;
                if (withAttendance)
                    pdfTable = new PdfPTable(table.getModel().getColumnCount());
                else pdfTable = new PdfPTable(table.getModel().getColumnCount() - 1);
                openPDF();
                addData(pdfTable);
                closePDF();
                fileException("Failas sėkmingai išsaugotas!");
            }
        } catch (FileNotFoundException | DocumentException ignored) {
            fileException("Failas tokiu vardu nerastas!");
        }
    }

    private void openPDF() throws FileNotFoundException, DocumentException {
        document = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(groupName + " studentų sarašas.pdf"));
        document.open();
    }

    private void closePDF() {
        document.close();
    }

    private void addData(PdfPTable pdfTable) throws DocumentException {
        pdfTable.setHeaderRows(1);

        PdfPCell cell;
        for (int i = 0; i < 3; i++) {
            cell = new PdfPCell(new Paragraph(table.getColumnName(i)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfTable.addCell(cell);
        }

        int columns = table.getModel().getColumnCount();
        if (withAttendance) {
            cell = new PdfPCell(new Paragraph(table.getColumnName(3)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfTable.addCell(cell);
        } else columns--;

        for (int i = 0; i < table.getModel().getRowCount(); i++)
            for (int j = 0; j < columns; j++) {
                if (table.getModel().getValueAt(i, j).toString().equals("true")) pdfTable.addCell("n");
                else if (table.getModel().getValueAt(i, j).toString().equals("false")) pdfTable.addCell("");
                else pdfTable.addCell(table.getModel().getValueAt(i, j).toString());
            }
        document.add(pdfTable);
    }
}
