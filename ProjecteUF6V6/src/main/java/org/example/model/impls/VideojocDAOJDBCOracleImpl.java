package org.example.model.impls;

import org.example.model.daos.DAO;
import org.example.model.entities.Videojoc;
import org.example.model.exceptions.DAOException;
import org.example.view.VideojocView;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.example.controller.Controller.PROP_EXCEPCIO;

/**
 * Classe VideojocDAOJDBCOracleImpl que implementa DAO<Videojoc>.
 * Gestiona l'accés a les dades dels videojocs a la base de dades.
 */
public class VideojocDAOJDBCOracleImpl implements DAO<Videojoc> {

    /**
     * Constructor de la classe VideojocDAOJDBCOracleImpl.
     * Crea la taula si no existeix i crea la seqüència i el trigger.
     *
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public VideojocDAOJDBCOracleImpl() throws DAOException {
        try {
            createTableIfNotExists();
            createTableAvgPegiIfNotExists();
            //createSequenceAndTrigger();
        } catch (DAOException ex) {
            JOptionPane.showMessageDialog(null, "Error al connectar a la BD!!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Mètode per obtenir un videojoc a partir del seu id.
     *
     * @param id L'id del videojoc.
     * @return El videojoc amb l'id especificat.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    @Override
    public Videojoc get(Long id) throws DAOException {

        //Declaració de variables del mètode
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        Videojoc videojoc = null;

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        //Accés a la BD usant l'API JDBC
        try {

            con = DriverManager.getConnection(
                    config.getUrl(),
                    config.getUsername(),
                    config.getPassword()
            );

            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM VIDEOJOCS;");
            if (rs.next()) {
                videojoc = new Videojoc(Long.valueOf(rs.getString(1)), rs.getString(2));
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                throw new DAOException(1);
            }

        }
        return videojoc;
    }

    /**
     * Mètode per obtenir tots els videojocs.
     *
     * @return Una llista amb tots els videojocs.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    @Override
    public List<Videojoc> getAll() throws DAOException {
        List<Videojoc> videojocs = new ArrayList<>();

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        )) {
            PreparedStatement st = con.prepareStatement("SELECT * FROM VIDEOJOCS ORDER BY id");
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Videojoc videojoc = new Videojoc(rs.getLong("id"), rs.getString("titol"), rs.getDouble("pegi"), rs.getString("multijugador").equals("t"),
                        new TreeSet<Videojoc.Equip>());

                // Get the associated Equip objects for this Videojoc
                PreparedStatement stEquip = con.prepareStatement("SELECT * FROM EQUIPS WHERE videojoc_id = ? ORDER BY id_equip");
                stEquip.setLong(1, videojoc.getId());
                ResultSet rsEquip = stEquip.executeQuery();

                while (rsEquip.next()) {
                    Videojoc.Equip equip = new Videojoc.Equip(Videojoc.Equip.Regio.valueOf(rsEquip.getString("regio")), rsEquip.getString("nom"), rsEquip.getLong("id_equip"), rsEquip.getLong("videojoc_id"));
                    videojoc.getMatricules().add(equip);
                }

                videojocs.add(videojoc);
            }
        } catch (SQLException throwables) {
            setExcepcio(new DAOException(0));
        }
        return videojocs;
    }

    /**
     * Mètode per inserir un videojoc a la base de dades.
     *
     * @param obj El videojoc a inserir.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    @Override
    public void insert(Videojoc obj) throws DAOException {
        String insertSQL = "INSERT INTO VIDEOJOCS (id, titol, pegi, multijugador) VALUES (?, ?, ?, ?)";
        String selectMaxIdSQL = "SELECT NVL(MAX(id), 0) + 1 FROM VIDEOJOCS";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement stSelectMaxId = con.prepareStatement(selectMaxIdSQL);
             PreparedStatement stInsert = con.prepareStatement(insertSQL)
        ) {
            ResultSet rs = stSelectMaxId.executeQuery();
            if (rs.next()) {
                long newId = rs.getLong(1);
                stInsert.setLong(1, newId);
                stInsert.setString(2, obj.getTitol());
                stInsert.setDouble(3, obj.getPegi());
                stInsert.setString(4, obj.isMultijugador() ? "t" : "f");
                stInsert.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    /**
     * Mètode per actualitzar un videojoc a la base de dades.
     *
     * @param obj El videojoc a actualitzar.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public void update(Videojoc obj) throws DAOException {
        String updateSQL = "UPDATE VIDEOJOCS SET titol = ?, pegi = ?, multijugador = ? WHERE id = ?";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement st = con.prepareStatement(updateSQL)
        ) {
            st.setString(1, obj.getTitol());
            st.setDouble(2, obj.getPegi());
            st.setString(3, obj.isMultijugador() ? "t" : "f");
            st.setLong(4, obj.getId());
            st.executeUpdate();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    /**
     * Mètode per eliminar un videojoc de la base de dades.
     *
     * @param obj El videojoc a eliminar.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    @Override
    public void delete(Videojoc obj) throws DAOException {
        String deleteEquipSQL = "DELETE FROM EQUIPS WHERE videojoc_id = ?";
        String deleteVideojocSQL = "DELETE FROM VIDEOJOCS WHERE id = ?";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement stDeleteEquip = con.prepareStatement(deleteEquipSQL);
             PreparedStatement stDeleteVideojoc = con.prepareStatement(deleteVideojocSQL)
        ) {
            // Primero borrem tots els equips associats al videojoc
            stDeleteEquip.setLong(1, getIdBD(obj));
            stDeleteEquip.executeUpdate();

            // Despres borrem el videojoc
            stDeleteVideojoc.setLong(1, getIdBD(obj));
            stDeleteVideojoc.executeUpdate();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    /**
     * Mètode per crear la taula si no existeix.
     *
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public void createTableIfNotExists() throws DAOException {
        String checkTableVideojocsSQL = "SELECT COUNT(*) FROM user_tables WHERE table_name = 'VIDEOJOCS'";
        String checkTableEquipsSQL = "SELECT COUNT(*) FROM user_tables WHERE table_name = 'EQUIPS'";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(), // Usar getUrl() para obtener la URL de la base de datos
                config.getUsername(), // Usar getUsername() para obtener el nombre de usuario
                config.getPassword() // Usar getPassword() para obtener la contraseña
        )) {

            // Comprobar i crear taula VIDEOJOCS si no existeix
            try (PreparedStatement st = con.prepareStatement(checkTableVideojocsSQL)) {
                ResultSet rs = st.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    try (CallableStatement cst = con.prepareCall("{call crear_taula_videojocs}")) {
                        cst.execute();
                    }
                }
            }

            // Comprobar i crear taula EQUIPS si no existeix
            try (PreparedStatement st = con.prepareStatement(checkTableEquipsSQL)) {
                ResultSet rs = st.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    try (CallableStatement cst = con.prepareCall("{call crear_taula_equips}")) {
                        cst.execute();
                    }
                }
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    /**
     * Mètode per obtenir l'id d'un videojoc a la base de dades.
     *
     * @param obj El videojoc.
     * @return L'id del videojoc a la base de dades.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public Long getIdBD(Videojoc obj) throws DAOException {
        String obtIdSQL = "SELECT id FROM VIDEOJOCS WHERE titol = ?";
        Long id = null;

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement st = con.prepareStatement(obtIdSQL)
        ) {
            st.setString(1, obj.getTitol());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    id = rs.getLong("id");
                }
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }

        return id;
    }

    /**
     * Mètode per inserir un equip a la base de dades.
     *
     * @param eq L'equip a inserir.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public void insertEquip(Videojoc.Equip eq) throws DAOException {
        if (eq.getNom() == null || eq.getNom().trim().isEmpty()) {
            throw new DAOException(1);
        }
        String insertSQL = "INSERT INTO EQUIPS (regio, nom, id_equip, videojoc_id) VALUES (?, ?, ?, ?)";
        String selectMaxIdEquipSQL = "SELECT NVL(MAX(id_equip), 0) + 1 FROM EQUIPS";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement stSelectMaxIdEquip = con.prepareStatement(selectMaxIdEquipSQL);
             PreparedStatement stInsert = con.prepareStatement(insertSQL)
        ) {
            ResultSet rs = stSelectMaxIdEquip.executeQuery();
            if (rs.next()) {
                long newIdEquip = rs.getLong(1);
                stInsert.setString(1, eq.getRegio().name());
                stInsert.setString(2, eq.getNom());
                stInsert.setLong(3, newIdEquip);
                stInsert.setLong(4, eq.getVideojoc_id());
                stInsert.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    /**
     * Mètode per comprovar si una regió existeix a la base de dades.
     *
     * @param regio La regió a comprovar.
     * @return true si la regió existeix, false en cas contrari.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public boolean regionExists(Videojoc.Equip.Regio regio) throws DAOException {
        String checkRegionSQL = "SELECT COUNT(*) FROM EQUIPS WHERE regio = ?";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement st = con.prepareStatement(checkRegionSQL)
        ) {
            st.setString(1, regio.name());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }

        return false;
    }

    /**
     * Mètode per modificar un equip a la base de dades.
     *
     * @param eq L'equip a modificar.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public void modificarEquip(Videojoc.Equip eq) throws DAOException {
        String updateSQL = "UPDATE EQUIPS SET regio = ?, nom = ? WHERE id_equip = ?";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement st = con.prepareStatement(updateSQL)
        ) {
            st.setString(1, eq.getRegio().name());
            st.setString(2, eq.getNom());
            st.setLong(3, eq.getId_equip());
            st.executeUpdate();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    /**
     * Mètode per obtenir l'id d'un equip a la base de dades.
     *
     * @param eq L'equip.
     * @return L'id de l'equip a la base de dades.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public Long getIDBDEquip(Videojoc.Equip eq) throws DAOException {
        String obtIdEquipSQL = "SELECT id_equip FROM EQUIPS WHERE regio = ? AND videojoc_id = ?";
        Long id = null;

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement st = con.prepareStatement(obtIdEquipSQL)
        ) {
            st.setString(1, eq.getRegio().name());
            st.setLong(2, eq.getVideojoc_id());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    id = rs.getLong("id_equip");
                }
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }

        return id;
    }

    /**
     * Mètode per comprovar si una regió existeix en un videojoc a la base de dades.
     *
     * @param regio      La regió a comprovar.
     * @param videojocId L'id del videojoc.
     * @return true si la regió existeix en el videojoc, false en cas contrari.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public boolean regionExistsInVideojoc(Videojoc.Equip.Regio regio, Long videojocId) throws DAOException {
        String checkRegionSQL = "SELECT COUNT(*) FROM EQUIPS WHERE regio = ? AND videojoc_id = ?";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement st = con.prepareStatement(checkRegionSQL)
        ) {
            st.setString(1, regio.name());
            st.setLong(2, videojocId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }

        return false;
    }

    /**
     * Mètode per eliminar un equip de la base de dades.
     *
     * @param equip L'equip a eliminar.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public void borrarEquip(Videojoc.Equip equip) throws DAOException {
        String deleteEquipSQL = "DELETE FROM EQUIPS WHERE id_equip = ?";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             PreparedStatement st = con.prepareStatement(deleteEquipSQL)
        ) {
            st.setLong(1, equip.getId_equip());
            st.executeUpdate();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    /**
     * Mètode per obtenir la mitja de pegi de tots els Videojocs de la taula.
     *
     * @return La mitja de pegi de tots els videojocs.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    public void updateAvgPegi() throws DAOException {
        String call = "{call update_avgPegi}";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             CallableStatement stmt = con.prepareCall(call)) {

            stmt.execute();
        } catch (SQLException e) {
            throw new DAOException(1);
        }
    }



    public void updateAvgPegifun() throws DAOException {
        String call = "{ ? = call calculate_avgPegi() }";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
             CallableStatement stmt = con.prepareCall(call)) {

            // Registrar el parámetro de salida
            stmt.registerOutParameter(1, Types.NUMERIC);

            // Ejecutar la llamada a la función
            stmt.execute();

            // Obtener el valor devuelto por la función
            double avgPegi = stmt.getDouble(1);

            // Utilizar el valor devuelto (por ejemplo, imprimirlo)
            System.out.println("La mitja PEGI es: " + avgPegi);

        } catch (SQLException e) {
            throw new DAOException(1);
        }
    }




    // Crear tabla avgPegi si no existe
    public void createTableAvgPegiIfNotExists() throws DAOException {
        String checkTableAvgPegiSQL = "SELECT COUNT(*) FROM user_tables WHERE table_name = 'AVGPEGI'";

        // Crear una instancia de ConfiguracioBaseDades
        org.example.model.prop.Properties.ConfiguracioBaseDades config = new org.example.model.prop.Properties().new ConfiguracioBaseDades();

        try (Connection con = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        )) {
            // Comprobar i crear taula AVGPEGI si no existeix
            try (PreparedStatement st = con.prepareStatement(checkTableAvgPegiSQL)) {
                ResultSet rs = st.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    try (CallableStatement cst = con.prepareCall("{call crear_taula_avgpegi}")) {
                        cst.execute();
                    }
                }
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }





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

    private VideojocView view;

    /**
     * Mètode que es crida quan es produeix un canvi en una propietat lligada.
     * En aquest cas, es tracta de l'excepció, i quan aquesta canvia, es llança i es tracta.
     *
     * @param evt L'esdeveniment que descriu l'origen i la propietat que ha canviat.
     */
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
                        case 2602:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        default:
                            // Para cualquier otro tipo de excepción, mostramos el mensaje de error
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                    }
            }
        }
    }

}