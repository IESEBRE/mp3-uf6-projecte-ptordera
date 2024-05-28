package org.example.app;

import org.example.controller.Controller;
import org.example.model.exceptions.DAOException;
import org.example.model.impls.VideojocDAOJDBCOracleImpl;
import org.example.view.VideojocView;

import javax.swing.*;
import java.util.Locale;

public class Main {


    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Definim la cultura de la nostra aplicació
                Locale.setDefault(new Locale("ca","ES"));
                try {
                    new Controller(new VideojocDAOJDBCOracleImpl(), new VideojocView());
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}