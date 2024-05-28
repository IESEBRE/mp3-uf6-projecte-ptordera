package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VideojocView extends JFrame{
    private JTabbedPane pestanyes;
    private JTable taula;
    private JScrollPane scrollPane1;
    private JButton insertarButton;
    private JButton modificarButton;
    private JButton borrarButton;
    private JTextField campTitol;
    private JTextField campPegi;
    private JCheckBox caixaMultijugador;
    private JPanel panel;
    private JTable taulaEqu;
    private JComboBox comboRegio;
    private JTextField campNom;
    //private JTabbedPane PanelPestanya;

    //Getters


    public JTable getTaulaEqu() {
        return taulaEqu;
    }

    public JComboBox getComboRg() {
        return comboRegio;
    }

    public JTextField getCampNom() {
        return campNom;
    }

    public JTabbedPane getPestanyes() {
        return pestanyes;
    }

    public JTable getTaula() {
        return taula;
    }

    public JButton getBorrarButton() {
        return borrarButton;
    }

    public JButton getModificarButton() {
        return modificarButton;
    }

    public JButton getInsertarButton() {
        return insertarButton;
    }

    public JTextField getCampTitol() {
        return campTitol;
    }

    public JTextField getCampPegi() {
        return campPegi;
    }

    public JCheckBox getCaixaMultijugador() {
        return caixaMultijugador;
    }


    //Constructor de la classe
    public VideojocView() {


        //Per poder vore la finestra
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(false);
    }

        private void createUIComponents() {
        // TODO: place custom component creation code here
        scrollPane1 = new JScrollPane();
        taula = new JTable();
        pestanyes = new JTabbedPane();
        taula.setModel(new DefaultTableModel());
        taula.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane1.setViewportView(taula);

    }
}
