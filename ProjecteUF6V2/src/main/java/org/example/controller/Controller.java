package org.example.controller;

import org.example.model.entities.Videojoc;
import org.example.model.exceptions.DAOException;
import org.example.model.entities.Videojoc.Equip;
import org.example.view.ModelComponentsVisuals;
import org.example.model.impls.VideojocDAOJDBCOracleImpl;
import org.example.view.VideojocView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class Controller implements PropertyChangeListener { //1. Implementació de interfície PropertyChangeListener


    private ModelComponentsVisuals modelComponentsVisuals =new ModelComponentsVisuals();
    private VideojocDAOJDBCOracleImpl dadesVideojocs;
    private VideojocView view;

    public Controller(VideojocDAOJDBCOracleImpl dadesVideojocs, VideojocView view) {
        this.dadesVideojocs = dadesVideojocs;
        this.view = view;

        //5. Necessari per a que Controller reaccione davant de canvis a les propietats lligades
        canvis.addPropertyChangeListener(this);

        lligaVistaModel();

        afegirListeners();

        //Si no hem tingut cap poroblema amb la BD, mostrem la finestra
        view.setVisible(true);

    }

    private void lligaVistaModel() {

        //Carreguem la taula d'alumnes en les dades de la BD
        try {
            setModelTaulaVideojoc(modelComponentsVisuals.getModelTaulaVideojoc(),dadesVideojocs.getAll());
        } catch (DAOException e) {
            this.setExcepcio(e);
        }

            //Fixem el model de la taula dels alumnes
        JTable taula = view.getTaula();
        taula.setModel(this.modelComponentsVisuals.getModelTaulaVideojoc());
        //Amago la columna que conté l'objecte alumne
//        taula.getColumnModel().getColumn(3).setMinWidth(0);
//        taula.getColumnModel().getColumn(3).setMaxWidth(0);
//        taula.getColumnModel().getColumn(3).setPreferredWidth(0);

        //Fixem el model de la taula de matrícules
        JTable taulaEqu = view.getTaulaEqu();
        taulaEqu.setModel(this.modelComponentsVisuals.getModelTaulaEqu());

        //Posem valor a el combo d'MPs
        view.getComboRg().setModel(modelComponentsVisuals.getComboBoxModel());

        //Desactivem la pestanya de la matrícula
        view.getPestanyes().setEnabledAt(1, false);
        view.getPestanyes().setTitleAt(1, "Equips de ...");

        //5. Necessari per a que Controller reaccione davant de canvis a les propietats lligades
        canvis.addPropertyChangeListener(this);
    }

    private void setModelTaulaVideojoc(DefaultTableModel modelTaulaVideojoc, List<Videojoc> all) {

        // Fill the table model with data from the collection
        for (Videojoc videojoc : all) {
            modelTaulaVideojoc.addRow(new Object[]{videojoc.getTitol(), videojoc.getPegi(), videojoc.isMultijugador(), videojoc});
        }
    }

    private void afegirListeners() {

        ModelComponentsVisuals modelo = this.modelComponentsVisuals;
        DefaultTableModel model = modelo.getModelTaulaVideojoc();
        DefaultTableModel modelEqu = modelo.getModelTaulaEqu();
        JTable taula = view.getTaula();
        JTable taulaEqu = view.getTaulaEqu();
        JButton insertarButton = view.getInsertarButton();
        JButton modificarButton = view.getModificarButton();
        JButton borrarButton = view.getBorrarButton();
        JTextField campTitol = view.getCampTitol();
        JTextField campPegi = view.getCampPegi();
        JCheckBox caixaMultijugador = view.getCaixaMultijugador();
        JTabbedPane pestanyes = view.getPestanyes();

        //Botó insertar
        view.getInsertarButton().addActionListener(
                new ActionListener() {
                    /**
                     * Invoked when an action occurs.
                     *
                     * @param e the event to be processed
                     */
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JTextField campTitol = view.getCampTitol();
                        JTextField campPegi = view.getCampPegi();
                        JCheckBox caixaMultijugador = view.getCaixaMultijugador();

                        if (pestanyes.getSelectedIndex() == 0) {        //Si estem a la pestanya del videojoc
                            //Comprovem que totes les caselles continguen informació
                            if (campTitol.getText().isBlank() || campPegi.getText().isBlank()) {
                                JOptionPane.showMessageDialog(null, "Falta omplir alguna dada!!");
                            } else {
                                try {
                                    NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());   //Creem un número que entèn la cultura que utilitza l'aplicació
                                    double pegi = num.parse(campPegi.getText().trim()).doubleValue();  //intentem convertir el text a double
                                    if (pegi < 1 || pegi > 100) throw new ParseException("", 0);
                                    Videojoc vj = new Videojoc(campTitol.getText(), pegi, caixaMultijugador.isSelected(), new TreeSet<Equip>());
                                    model.addRow(new Object[]{campTitol.getText(), pegi, caixaMultijugador.isSelected(), vj});
                                    campTitol.setText("");
                                    campTitol.setSelectionStart(0);
                                    campTitol.setSelectionEnd(campTitol.getText().length());
                                    campPegi.setText("");
                                    campTitol.requestFocus();         //intentem que el foco vaigue al camp del titol
                                    dadesVideojocs.insert(vj);
                                } catch (ParseException | DAOException ex) {
                                    setExcepcio(new DAOException(3));
//                                    JOptionPane.showMessageDialog(null, "Has d'introduir un pes correcte (>=1 i <=800!!");
                                    campPegi.setSelectionStart(0);
                                    campPegi.setSelectionEnd(campPegi.getText().length());
                                    campPegi.requestFocus();
                                }
                            }
                        } else {         //Si estem a la pestanya de la matricula
                            //Obtinc l'alumne de la columna que conté l'objecte
                            Videojoc vj = (Videojoc) model.getValueAt(taula.getSelectedRow(), 3);
                            String nom = view.getCampNom().getText();

                            // Comprobem si el nom está en blanc
                            if (nom.isBlank()) {
                                JOptionPane.showMessageDialog(null, "El camp 'Nom' no pot estar en blanc!");
                            } else {
                                Equip eq = new Equip((Equip.Regio) view.getComboRg().getSelectedItem(), nom);
                                vj.getMatricules().add(eq);
                                ompliEquip(vj, modelEqu);
                            }
                            // Posem els camps en blanc
                            view.getCampNom().setText("");
                            view.getCampNom().setSelectionStart(0);
                            view.getCampNom().setSelectionEnd(view.getCampNom().getText().length());
                            view.getCampNom().requestFocus();
                            view.getComboRg().setSelectedIndex(0);


                        }


                    }
                }
        );

        view.getModificarButton().addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JTextField campTitol = view.getCampTitol();
                        JTextField campPegi = view.getCampPegi();
                        JCheckBox caixaMultijugador = view.getCaixaMultijugador();

                        if (pestanyes.getSelectedIndex() == 0) { // Si estamos en la pestaña del videojuego
                            int filaSel = taula.getSelectedRow();
                            if (filaSel != -1) { // Tenemos una fila seleccionada
                                if (campTitol.getText().isBlank() || campPegi.getText().isBlank()) {
                                    JOptionPane.showMessageDialog(null, "¡Falta omplir alguna dada!");
                                } else {
                                    try {
                                        NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());
                                        double pegi = num.parse(campPegi.getText().trim()).doubleValue();
                                        if (pegi < 1 || pegi > 100) throw new ParseException("", 0);
                                        Videojoc vj = (Videojoc) model.getValueAt(filaSel, 3);
                                        vj.setTitol(campTitol.getText());
                                        vj.setPegi(pegi);
                                        vj.setMultijugador(caixaMultijugador.isSelected());
                                        model.setValueAt(campTitol.getText(), filaSel, 0);
                                        model.setValueAt(pegi, filaSel, 1);
                                        model.setValueAt(caixaMultijugador.isSelected(), filaSel, 2);
                                        dadesVideojocs.update(vj);
                                    } catch (ParseException | DAOException ex) {
                                        setExcepcio(new DAOException(3));
                                        campPegi.setSelectionStart(0);
                                        campPegi.setSelectionEnd(campPegi.getText().length());
                                        campPegi.requestFocus();
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "No s'ha seleccionat cap videojoc per modificar!");
                            }
                        } else { // Si estamos en la pestaña de la matrícula
                            // Aquí puedes agregar el código para modificar la matrícula si es necesario
                        }
                    }
                }
        );

        view.getBorrarButton().addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (pestanyes.getSelectedIndex() == 0) { // Si estem a la pestanya de videojoc
                            int filaSel = taula.getSelectedRow();
                            if (filaSel != -1) { // Tenemos una fila seleccionada
                                Videojoc vj = (Videojoc) model.getValueAt(filaSel, 3);
                                try {
                                    dadesVideojocs.delete(vj);
                                    model.removeRow(filaSel);
                                    //Posem els camps en blanc
                                    campTitol.setText("");
                                    campPegi.setText("");
                                    caixaMultijugador.setSelected(false);
                                } catch (DAOException ex) {
                                    setExcepcio(new DAOException(1));
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "No s'ha seleccionat cap videojoc per borrar!");
                            }
                        } else { // Si estamos en la pestaña de la matrícula
                            // Aquí puedes agregar el código para borrar la matrícula si es necesario
                        }
                    }
                }
        );

        taula.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                //Obtenim el número de la fila seleccionada
                int filaSel = taula.getSelectedRow();

                if (filaSel != -1) {        //Tenim una fila seleccionada
                    //Posem els valors de la fila seleccionada als camps respectius
                    campTitol.setText(model.getValueAt(filaSel, 0).toString());
                    campPegi.setText(model.getValueAt(filaSel, 1).toString().replaceAll("\\.", ","));
                    caixaMultijugador.setSelected((Boolean) model.getValueAt(filaSel, 2));

                    //Activem la pestanya de la matrícula de l'alumne seleccionat
                    view.getPestanyes().setEnabledAt(1, true);
                    view.getPestanyes().setTitleAt(1, "Equips de " + campTitol.getText());

                    //Posem valor a el combo d'MPs
                    //view.getComboMP().setModel(modelo.getComboBoxModel());
                    ompliEquip((Videojoc) model.getValueAt(filaSel, 3),modelEqu);
                } else {                  //Hem deseleccionat una fila
                    //Posem els camps de text en blanc
                    campTitol.setText("");
                    campPegi.setText("");

                    //Desactivem pestanyes
                    view.getPestanyes().setEnabledAt(1, false);
                    view.getPestanyes().setTitleAt(1, "Equips de ...");
                }
            }
        });

        campTitol.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                String regex1 = "^[A-ZÀ-ÚÑÇ][a-zA-Zà-úñç]*$";
                //String regex="[À-ú]";
                //Pattern pattern = Pattern.compile(regex);
                if(campTitol.getText().isBlank() || (!campTitol.getText().matches(regex1))){
                    setExcepcio(new DAOException(1403));
                }
            }
        });
        //throw new LaMeuaExcepcio(1,"Ha petat la base de dades");
    }



    private static void ompliEquip(Videojoc al, DefaultTableModel modelMat) {
        //Omplim el model de la taula de matrícula de l'alumne seleccionat
        modelMat.setRowCount(0);
        // Fill the table model with data from the collection
        for (Equip equip : al.getMatricules()) {
            modelMat.addRow(new Object[]{equip.getRegio(), equip.getNom()});
        }
    }


    //TRACTAMENT D'EXCEPCIONS

    //2. Propietat lligada per controlar quan genero una excepció
    public static final String PROP_EXCEPCIO="excepcio";
    private DAOException excepcio;

    public DAOException getExcepcio() {
        return excepcio;
    }

    public void setExcepcio(DAOException excepcio) {
        DAOException valorVell=this.excepcio;
        this.excepcio = excepcio;
        canvis.firePropertyChange(PROP_EXCEPCIO, valorVell,excepcio);
    }


    //3. Propietat PropertyChangesupport necessària per poder controlar les propietats lligades
    PropertyChangeSupport canvis=new PropertyChangeSupport(this);


    //4. Mètode on posarem el codi de tractament de les excepcions --> generat per la interfície PropertyChangeListener
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DAOException rebuda=(DAOException)evt.getNewValue();

        try {
            throw rebuda;
        } catch (DAOException e) {
            //Aquí farem ele tractament de les excepcions de l'aplicació
            switch(evt.getPropertyName()){
                case PROP_EXCEPCIO:

                    switch(rebuda.getTipo()){
                        case 0:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            System.exit(1);
                            break;
                        case 1:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 2:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            //this.view.getCampNom().setText(rebuda.getMissatge());
                            this.view.getCampTitol().setSelectionStart(0);
                            this.view.getCampTitol().setSelectionEnd(this.view.getCampTitol().getText().length());
                            this.view.getCampTitol().requestFocus();

                            break;
                    }


            }
        }
    }

}
