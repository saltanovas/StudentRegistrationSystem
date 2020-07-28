package com.aivarassaltanovas.studenturegistracija;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

import static com.aivarassaltanovas.studenturegistracija.MainWindow.DARK_BLUE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.ORANGE;
import static com.aivarassaltanovas.studenturegistracija.MainWindow.WHITE;


abstract class UserNotifications {

    public void deleteMessage(JLabel label){
        label.setText("");
    }

    public void emptyInput(JLabel label){
        label.setForeground(Color.RED);
        label.setText("Prašome užpildyti visus laukelius!");
    }

    public void successfulInput(JLabel label, String name, String surname, String group){
        label.setForeground(Color.BLACK);
        if(("a").equals(name.substring(name.length() - 1)) || ("ė").equals(name.substring(name.length() - 1)) || ("e").equals(name.substring(name.length() - 1))){
            label.setText(name + " " + surname + " įtraukta į " + group + " grupę");
        } else label.setText(name + " " + surname + " įtrauktas į " + group + " grupę");
    }

    public void studentAlreadyExist(JLabel label, String group){
        label.setForeground(Color.RED);
        label.setText("Toks studentas egzistuoja " + group + " grupėje");
    }

    public void groupAlreadyExist(JLabel label, String group){
        label.setForeground(Color.RED);
        label.setText(group + " grupė jau egzistuoja");
    }

    public void emptyGroup(JLabel label){
        label.setForeground(Color.RED);
        label.setText("Grupė nebuvo įvesta");
    }

    public void successfulEdit(JLabel label, String group, String newGroup){
        label.setForeground(Color.BLACK);
        label.setText(group + " pavadinimas pakeistas į " + newGroup);
    }

    public void successfulRemove(JLabel label, String group){
        label.setForeground(Color.BLACK);
        label.setText(group + " sėkmingai pašalinta iš sąrašo");
    }

    public void fileException(String text){
        UIManager.put("OptionPane.background", DARK_BLUE);
        UIManager.put("OptionPane.messageForeground", WHITE);
        UIManager.put("Panel.background", DARK_BLUE);
        UIManager.put("Button.background", ORANGE);
        UIManager.put("Button.focus", false);
        UIManager.put("Button.border", Color.WHITE);
        UIManager.put("OptionPane.okButtonText", "   GERAI   ");
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("SansSerif", Font.PLAIN, 15)));
        ImageIcon img = new ImageIcon(getClass().getResource("fileExceptionIcon.png"));
        JOptionPane.showMessageDialog(null, text, "Nepasisekė!", JOptionPane.INFORMATION_MESSAGE, img);
    }
}
