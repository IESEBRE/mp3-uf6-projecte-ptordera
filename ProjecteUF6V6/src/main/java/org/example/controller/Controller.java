package org.example.controller;

import org.example.model.entities.Videojoc;
import org.example.model.exceptions.DAOException;
import org.example.model.entities.Videojoc.Equip;
import org.example.view.ModelComponentsVisuals;
import org.example.model.impls.VideojocDAOJDBCOracleImpl;
import org.example.view.VideojocView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

/**
 * Classe Controller que gestiona les interaccions entre la vista i el model.
 */
public class Controller implements PropertyChangeListener { //1. Implementació de interfície PropertyChangeListener


    private ModelComponentsVisuals modelComponentsVisuals = new ModelComponentsVisuals();
    private VideojocDAOJDBCOracleImpl dadesVideojocs;
    private VideojocView view;

    /**
     * Constructor de la classe Controller.
     * Inicialitza les dades dels videojocs, la vista, lliga la vista amb el model i afegeix els listeners.
     *
     * @param dadesVideojocs Les dades dels videojocs.
     * @param view           La vista.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public Controller(VideojocDAOJDBCOracleImpl dadesVideojocs, VideojocView view) throws DAOException {
        this.dadesVideojocs = dadesVideojocs;
        this.view = view;

        lligaVistaModel();

        afegirListeners();

        dadesVideojocs.updateAvgPegifun();
        //Si no hem tingut cap poroblema amb la BD, mostrem la finestra
        view.setVisible(true);

    }

    /**
     * Mètode que lliga la vista amb el model.
     * Carrega les dades de la base de dades a la taula de videojocs i equips, i estableix els models de les taules.
     *
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    private void lligaVistaModel() throws DAOException {

        //Carreguem la taula Videojocs en les dades de la BD
        try {
            setModelTaulaVideojoc(modelComponentsVisuals.getModelTaulaVideojoc(), dadesVideojocs.getAll());
        } catch (DAOException e) {
            this.setExcepcio(e);
        }

        //Carreguem la taula Equips en les dades de la BD
        setModelTaulaEqu(modelComponentsVisuals.getModelTaulaEqu(), dadesVideojocs.getAll());

        //Fixem el model de la taula dels Videojocs
        JTable taula = view.getTaula();
        JTable taulaEqu = view.getTaulaEqu();
        taula.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taulaEqu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taula.setModel(this.modelComponentsVisuals.getModelTaulaVideojoc());
        //Oculto la columna que conté l'objecte Videojoc i l'ID
//        taula.getColumnModel().getColumn(3).setMinWidth(0);
//        taula.getColumnModel().getColumn(3).setMaxWidth(0);
//        taula.getColumnModel().getColumn(3).setPreferredWidth(0);
//        taula.getColumnModel().getColumn(4).setMinWidth(0);
//        taula.getColumnModel().getColumn(4).setMaxWidth(0);
//        taula.getColumnModel().getColumn(4).setPreferredWidth(0);


        //Fixem el model de la taula de matrícules
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
            modelTaulaVideojoc.addRow(new Object[]{videojoc.getTitol(), videojoc.getPegi(), videojoc.isMultijugador(), videojoc, videojoc.getId()});
        }
    }

    private void setModelTaulaEqu(DefaultTableModel modelTaulaEqu, List<Videojoc> all) {
        // Fill the table model with data from the collection
        for (Videojoc videojoc : all) {
            for (Equip equip : videojoc.getMatricules()) {
                modelTaulaEqu.addRow(new Object[]{equip.getRegio(), equip.getNom(), equip.getId_equip(), equip.getVideojoc_id()});
            }
        }
    }

    /**
     * Mètode que afegeix els listeners als botons i taules de la vista.
     */
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

                        String regex1 = "^[A-ZÀ-ÚÑÇ][a-zA-Zà-úñç0-9]*$";
                        if (!campTitol.getText().isBlank() && (!campTitol.getText().matches(regex1))) {
                            setExcepcio(new DAOException(657));
                            return;
                        }

                        if (pestanyes.getSelectedIndex() == 0) {        //Si estem a la pestanya del videojoc
                            //Comprovem que totes les caselles continguen informació
                            if (campTitol.getText().isBlank() || campPegi.getText().isBlank()) {
                                setExcepcio(new DAOException(57));
                            } else {
                                try {
                                    String titolRepe = campTitol.getText();
                                    for (int i = 0; i < model.getRowCount(); i++) {
                                        if (titolRepe.equals(model.getValueAt(i, 0))) {
                                            JOptionPane.showMessageDialog(null, "Aquest Videojoc ja està inserit a la taula!");
                                            return;
                                        }
                                    }
                                    NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());   //Creem un número que entèn la cultura que utilitza l'aplicació
                                    double pegi = num.parse(campPegi.getText().trim()).doubleValue();  //intentem convertir el text a double
                                    if (pegi < 0 || pegi > 100) throw new ParseException("", 0);
                                    Videojoc vj = new Videojoc(campTitol.getText(), pegi, caixaMultijugador.isSelected(), new TreeSet<Equip>());
                                    String title = campTitol.getText();
                                    campTitol.setText("");
                                    campTitol.setSelectionStart(0);
                                    campTitol.setSelectionEnd(campTitol.getText().length());
                                    campPegi.setText("");
                                    campTitol.requestFocus();         //intentem que el foco vaigue al camp del titol
                                    dadesVideojocs.insert(vj);

                                    // Obtenim l'ID del videojoc que acabem d'insertar
                                    long newId = dadesVideojocs.getIdBD(vj);

                                    // Establim l'ID del videojoc a la taula
                                    vj.setId(newId);

                                    model.addRow(new Object[]{title, pegi, caixaMultijugador.isSelected(), vj, newId});
                                    dadesVideojocs.updateAvgPegi();
                                    dadesVideojocs.updateAvgPegifun();

                                } catch (ParseException | DAOException ex) {
                                    setExcepcio(new DAOException(3));
                                    campPegi.setSelectionStart(0);
                                    campPegi.setSelectionEnd(campPegi.getText().length());
                                    campPegi.requestFocus();
                                }
                            }
                        } else {

                            Videojoc vj = (Videojoc) model.getValueAt(taula.getSelectedRow(), 3);
                            String nom = view.getCampNom().getText();

                            // Codi per a que les dades dels camps s'insereixquin a la taula equips
                            if (campTitol.getText().isBlank() || campPegi.getText().isBlank()) {
                                setExcepcio(new DAOException(57));
                            } else {
                                Equip.Regio regio = (Equip.Regio) view.getComboRg().getSelectedItem();
                                try {
                                    if (dadesVideojocs.regionExistsInVideojoc(regio, vj.getId())) {
                                        setExcepcio(new DAOException(386));
                                    } else {
                                        try {
                                            if (campTitol.getText().isBlank() || campPegi.getText().isBlank() || view.getCampNom().getText().isBlank()) {
                                                setExcepcio(new DAOException(57));
                                            } else {
                                                // Inserta el objeto Equip en la base de datos sin un ID
                                                long videojoc_id = vj.getId();
                                                Equip eq = new Equip(regio, nom, 0, videojoc_id); // 0 es un valor temporal para id_equip
                                                dadesVideojocs.insertEquip(eq);

                                                // Después de la inserción, consulta la base de datos para obtener el ID generado
                                                long newIdEquip = dadesVideojocs.getIDBDEquip(eq);

                                                // Establece el ID obtenido en el objeto Equip
                                                eq.setId_equip(newIdEquip);

                                                vj.addEquip(eq);

                                                // Añade una nueva fila a la tabla con los datos del Equip
                                                modelEqu.addRow(new Object[]{eq.getRegio(), eq.getNom(), eq.getId_equip(), eq.getVideojoc_id()});

                                                // Limpiamos los campos de texto
                                                view.getCampNom().setText("");
                                                view.getComboRg().setSelectedIndex(0);
                                                dadesVideojocs.updateAvgPegi();
                                                dadesVideojocs.updateAvgPegifun();
                                            }
                                        } catch (DAOException ex) {
                                            setExcepcio(new DAOException(3));
                                            view.getCampNom().setSelectionStart(0);
                                            view.getCampNom().setSelectionEnd(view.getCampNom().getText().length());
                                            view.getCampNom().requestFocus();
                                        }
                                    }


                                } catch (DAOException ex) {
                                    setExcepcio(new DAOException(3));
                                    campPegi.setSelectionStart(0);
                                    campPegi.setSelectionEnd(campPegi.getText().length());
                                    campPegi.requestFocus();
                                }
                            }

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
                            if (filaSel != -1) { // Tenim una fila seleccionada
                                if (campTitol.getText().isBlank() || campPegi.getText().isBlank()) {
                                    setExcepcio(new DAOException(57));
                                } else {
                                    try {
                                        NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());
                                        double pegi = num.parse(campPegi.getText().trim()).doubleValue();
                                        if (pegi < 0 || pegi > 100) throw new ParseException("", 0);
                                        Videojoc vj = (Videojoc) model.getValueAt(filaSel, 3);
                                        vj.setTitol(campTitol.getText());
                                        vj.setPegi(pegi);
                                        vj.setMultijugador(caixaMultijugador.isSelected());
                                        model.setValueAt(campTitol.getText(), filaSel, 0);
                                        model.setValueAt(pegi, filaSel, 1);
                                        model.setValueAt(caixaMultijugador.isSelected(), filaSel, 2);
                                        //Posem el nou nom a la pestanya
                                        pestanyes.setTitleAt(1, "Equips de " + campTitol.getText());
                                        //taula.clearSelection();
                                        dadesVideojocs.update(vj);
                                        dadesVideojocs.updateAvgPegi();
                                        dadesVideojocs.updateAvgPegifun();
                                    } catch (ParseException | DAOException ex) {
                                        setExcepcio(new DAOException(4));
                                        campPegi.setSelectionStart(0);
                                        campPegi.setSelectionEnd(campPegi.getText().length());
                                        campPegi.requestFocus();
                                    }
                                }
                            } else {
                                setExcepcio(new DAOException(1065));
                            }
                        } else { // Si estamos en la pestaña del equipo
                            int filaSelEqu = taulaEqu.getSelectedRow();
                            int filaSelVj = taula.getSelectedRow();

                            if (filaSelEqu != -1 && filaSelVj != -1) { // Tenemos una fila seleccionada en ambas tablas
                                if (view.getCampNom().getText().isBlank()) {
                                    setExcepcio(new DAOException(57));
                                } else {
                                    try {
                                        // Obtenim l'objecte Videojoc de la fila seleccionada a la taula videojocs
                                        Videojoc vj = (Videojoc) model.getValueAt(filaSelVj, 3);

                                        // Obtenim l'objecte Equip corresponent a l'objecte Videojoc
                                        Equip equip = vj.getEquipById((long) modelEqu.getValueAt(filaSelEqu, 2));

                                        // Obtenim la regió que intentem modificar
                                        Equip.Regio regioModificado = (Equip.Regio) view.getComboRg().getSelectedItem();

                                        // Verifiquem si la regió ja existeix al Videojoc i no és la mateixa que l'actual
                                        if (!equip.getRegio().equals(regioModificado) && dadesVideojocs.regionExistsInVideojoc(regioModificado, vj.getId())) {
                                            setExcepcio(new DAOException(386));
                                        } else {
                                            // Actualitzem els valors de l'objecte Equip
                                            equip.setRegio(regioModificado);
                                            equip.setNom(view.getCampNom().getText());

                                            // Actualitzem l'objecte Equip a la base de dades
                                            dadesVideojocs.modificarEquip(equip);

                                            // Actualitzem l'objecte Videojoc a la taula videojocs
                                            model.setValueAt(vj, filaSelVj, 3);

                                            // Actualitzem la taua d'equips
                                            modelEqu.setValueAt(view.getComboRg().getSelectedItem(), filaSelEqu, 0);
                                            modelEqu.setValueAt(view.getCampNom().getText(), filaSelEqu, 1);
                                            dadesVideojocs.updateAvgPegi();
                                            dadesVideojocs.updateAvgPegifun();
                                        }
                                    } catch (DAOException ex) {
                                        setExcepcio(new DAOException(4));
                                        view.getCampNom().setSelectionStart(0);
                                        view.getCampNom().setSelectionEnd(view.getCampNom().getText().length());
                                        view.getCampNom().requestFocus();
                                    }
                                }
                            } else {
                                setExcepcio(new DAOException(1065));
                            }

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
                                    desactivarPestanya(pestanyes, 1);
                                    dadesVideojocs.updateAvgPegi();
                                    dadesVideojocs.updateAvgPegifun();
                                } catch (DAOException ex) {
                                    setExcepcio(new DAOException(5));
                                }
                            } else {
                                setExcepcio(new DAOException(1066));
                            }
                        } else { // Si estamos en la pestaña de la matrícula
                            // Vamos a hacer que se borre la fila seleccionada de la tabla equips
                            int filaSelEqu = taulaEqu.getSelectedRow();
                            int filaSelVj = taula.getSelectedRow();

                            if (filaSelEqu != -1 && filaSelVj != -1) { // Tenemos una fila seleccionada en ambas tablas
                                // Obtenemos el objeto Videojoc de la fila seleccionada en la tabla videojocs
                                Videojoc vj = (Videojoc) model.getValueAt(filaSelVj, 3);

                                // Obtenemos el objeto Equip correspondiente en el objeto Videojoc
                                Equip equip = vj.getEquipById((long) modelEqu.getValueAt(filaSelEqu, 2));

                                try {
                                    // Borramos el objeto Equip de la base de datos
                                    dadesVideojocs.borrarEquip(equip);

                                    // Borramos el objeto Equip de la colección del objeto Videojoc
                                    vj.removeEquip(equip);

                                    // Borramos la fila seleccionada de la tabla equips
                                    modelEqu.removeRow(filaSelEqu);

                                    // Limpiamos los campos de texto
                                    view.getCampNom().setText("");
                                    view.getComboRg().setSelectedIndex(0);
                                    dadesVideojocs.updateAvgPegi();
                                    dadesVideojocs.updateAvgPegifun();
                                } catch (DAOException ex) {
                                    setExcepcio(new DAOException(5));
                                }
                            } else {
                                setExcepcio(new DAOException(1066));
                            }
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
                    ompliEquip((Videojoc) model.getValueAt(filaSel, 3), modelEqu);
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

        taulaEqu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // Obtenim el número de la fila seleccionada
                int filaSel = taulaEqu.getSelectedRow();

                if (filaSel != -1) { // Tenim una fila seleccionada
                    // Obtenim els valors de la fila seleccionada
                    Equip.Regio regio = (Equip.Regio) modelEqu.getValueAt(filaSel, 0);
                    String nom = (String) modelEqu.getValueAt(filaSel, 1);

                    // Establim els valors als camps corresponents
                    view.getCampNom().setText(nom);
                    view.getComboRg().setSelectedItem(regio);
                } else {
                    // Limpiem els camps si no hi ha cap fila seleccionada
                    view.getCampNom().setText("");
                    view.getComboRg().setSelectedIndex(0);
                }
            }
        });
    }

    /**
     * Mètode que desactiva una pestanya de la vista.
     *
     * @param pestanyes El conjunt de pestanyes de la vista.
     * @param index     L'índex de la pestanya a desactivar.
     */
    private static void desactivarPestanya(JTabbedPane pestanyes, int index) {
        pestanyes.setEnabledAt(index, false);
        pestanyes.setTitleAt(index, "Equips de ...");
    }


    /**
     * Mètode que omple la taula d'equips amb les dades del videojoc seleccionat.
     *
     * @param vj       El videojoc seleccionat.
     * @param modelMat El model de la taula d'equips.
     */
    private static void ompliEquip(Videojoc vj, DefaultTableModel modelMat) {
        //Omplim el model de la taula de matrícula de l'alumne seleccionat
        modelMat.setRowCount(0);
        // Fill the table model with data from the collection
        for (Equip equip : vj.getMatricules()) {
            modelMat.addRow(new Object[]{equip.getRegio(), equip.getNom(), equip.getId_equip(), equip.getVideojoc_id()});
        }
    }


    //TRACTAMENT D'EXCEPCIONS

    //2. Propietat lligada per controlar quan genero una excepció
    public static final String PROP_EXCEPCIO = "excepcio";
    private DAOException excepcio;

    /**
     * Mètode per obtenir l'excepció actual.
     *
     * @return L'excepció actual.
     */
    public DAOException getExcepcio() {
        return excepcio;
    }

    /**
     * Mètode per establir una nova excepció.
     *
     * @param excepcio La nova excepció.
     */
    public void setExcepcio(DAOException excepcio) {
        DAOException valorVell = this.excepcio;
        this.excepcio = excepcio;
        canvis.firePropertyChange(PROP_EXCEPCIO, valorVell, excepcio);
    }


    //3. Propietat PropertyChangesupport necessària per poder controlar les propietats lligades
    PropertyChangeSupport canvis = new PropertyChangeSupport(this);


    //4. Mètode on posarem el codi de tractament de les excepcions --> generat per la interfície PropertyChangeListener

    /**
     * Mètode que es crida quan es produeix un canvi en una propietat lligada.
     * En aquest cas, es tracta de l'excepció, i quan aquesta canvia, es llança i es tracta.
     *
     * @param evt L'esdeveniment que descriu l'origen i la propietat que ha canviat.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DAOException rebuda = (DAOException) evt.getNewValue();

        try {
            throw rebuda;
        } catch (DAOException e) {
            //Aquí farem ele tractament de les excepcions de l'aplicació
            switch (evt.getPropertyName()) {
                case PROP_EXCEPCIO:

                    switch (rebuda.getTipo()) {
                        case 0:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            System.exit(1);
                            break;
                        case 1:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 3:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 4:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 5:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 57:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 386:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 657:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            //this.view.getCampNom().setText(rebuda.getMissatge());
                            this.view.getCampTitol().setSelectionStart(0);
                            this.view.getCampTitol().setSelectionEnd(this.view.getCampTitol().getText().length());
                            this.view.getCampTitol().requestFocus();

                            break;
                        case 1065:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 1066:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                    }


            }
        }
    }

}
