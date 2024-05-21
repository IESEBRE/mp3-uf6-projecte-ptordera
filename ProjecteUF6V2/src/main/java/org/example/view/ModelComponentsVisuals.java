package org.example.view;

import org.example.model.entities.Videojoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ModelComponentsVisuals {

    private DefaultTableModel modelTaulaVideojoc;
    private DefaultTableModel modelTaulaEqu;
    private ComboBoxModel<Videojoc.Equip.Regio> comboBoxModel;

    //Getters


    public ComboBoxModel<Videojoc.Equip.Regio> getComboBoxModel() {
        return comboBoxModel;
    }

    public DefaultTableModel getModelTaulaVideojoc() {
        return modelTaulaVideojoc;
    }

    public DefaultTableModel getModelTaulaEqu() {
        return modelTaulaEqu;
    }

    public ModelComponentsVisuals() {


        //Anem a definir l'estructura de la taula dels alumnes
        modelTaulaVideojoc =new DefaultTableModel(new Object[]{"Titol","PEGI","És multijugador?","Object"},0){
            /**
             * Returns true regardless of parameter values.
             *
             * @param row    the row whose value is to be queried
             * @param column the column whose value is to be queried
             * @return true
             * @see #setValueAt
             */
            @Override
            public boolean isCellEditable(int row, int column) {

                //Fem que TOTES les cel·les de la columna 1 de la taula es puguen editar
                //if(column==1) return true;
                return false;
            }



            //Permet definir el tipo de cada columna
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Double.class;
                    case 2:
                        return Boolean.class;
                    default:
                        return Object.class;
                }
            }
        };




        //Anem a definir l'estructura de la taula de les matrícules
        modelTaulaEqu =new DefaultTableModel(new Object[]{"Regió","Nom"},0){
            /**
             * Returns true regardless of parameter values.
             *
             * @param row    the row whose value is to be queried
             * @param column the column whose value is to be queried
             * @return true
             * @see #setValueAt
             */
            @Override
            public boolean isCellEditable(int row, int column) {

                //Fem que TOTES les cel·les de la columna 1 de la taula es puguen editar
                //if(column==1) return true;
                return false;
            }

            //Permet definir el tipo de cada columna
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Videojoc.Equip.Regio.class;
                    case 1:
                        return Integer.class;
                    default:
                        return Object.class;
                }
            }
        };



        //Estructura del comboBox
        comboBoxModel=new DefaultComboBoxModel<>(Videojoc.Equip.Regio.values());



    }
}
