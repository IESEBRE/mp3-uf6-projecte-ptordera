package org.example.model.impls;

import org.example.model.daos.DAO;
import org.example.model.entities.Videojoc;
import org.example.model.exceptions.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class VideojocDAOJDBCOracleImpl implements DAO<Videojoc> {

    public VideojocDAOJDBCOracleImpl() {
        try {
            createTableIfNotExists();
            createSequenceAndTrigger();
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Videojoc get(Long id) throws DAOException {

        //Declaració de variables del mètode
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        Videojoc videojoc = null;

        //Accés a la BD usant l'API JDBC
        try {

            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//localhost:1521/xe",
                    "C##HR",
                    "HR"
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

    @Override
    public List<Videojoc> getAll() throws DAOException {
        //Declaració de variables del mètode
        List<Videojoc> videojocs = new ArrayList<>();

        //Accés a la BD usant l'API JDBC
        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st = con.prepareStatement("SELECT * FROM VIDEOJOCS");
             ResultSet rs = st.executeQuery();
        ) {

            while (rs.next()) {
                videojocs.add(new Videojoc(rs.getLong("id"), rs.getString("titol"), rs.getDouble("pegi"),
                        new TreeSet<Videojoc.Equip>()));
            }
        } catch (SQLException throwables) {
            int tipoError = throwables.getErrorCode();
            //System.out.println(tipoError+" "+throwables.getMessage());
            switch (throwables.getErrorCode()) {
                case 17002: //l'he obtingut posant un sout en el throwables.getErrorCode()
                    tipoError = 0;
                    break;
                default:
                    tipoError = 1;  //error desconegut
            }
            throw new DAOException(tipoError);
        }
        return videojocs;
    }

    @Override
    public void insert(Videojoc obj) throws DAOException {
        String insertSQL = "INSERT INTO VIDEOJOCS (titol, pegi, multijugador) VALUES (?, ?, ?)";

        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st = con.prepareStatement(insertSQL)
        ) {
            st.setString(1, obj.getTitol());
            st.setDouble(2, obj.getPegi());
            st.setString(3, obj.isMultijugador()? "t" : "f");
            st.executeUpdate();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    public void update(Videojoc obj) throws DAOException {
        String updateSQL = "UPDATE VIDEOJOCS SET titol = ?, pegi = ?, multijugador = ? WHERE id = ?";

        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st = con.prepareStatement(updateSQL)
        ) {
            st.setString(1, obj.getTitol());
            st.setDouble(2, obj.getPegi());
            st.setString(3, obj.isMultijugador()? "t" : "f");
            st.executeUpdate();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    @Override
    public void delete(Videojoc obj) throws DAOException {
        String deleteSQL = "DELETE FROM VIDEOJOCS WHERE id = ?";

        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st = con.prepareStatement(deleteSQL)
        ) {
            st.executeUpdate();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    public void createTableIfNotExists() throws DAOException {
        String createTableSQL = "DECLARE " +
                "   count_tables NUMBER(1); " +
                "BEGIN " +
                "   SELECT COUNT(*) INTO count_tables FROM user_tables WHERE table_name = 'VIDEOJOCS'; " +
                "   IF count_tables = 0 THEN " +
                "       EXECUTE IMMEDIATE 'CREATE TABLE VIDEOJOCS ( " +
                "           id NUMBER(10) PRIMARY KEY, " +
                "           titol VARCHAR2(100), " +
                "           pegi NUMBER(10, 2), " +
                "           multijugador CHAR(1 CHAR) " +
                "       )'; " +
                "   END IF; " +
                "END;";

        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st = con.prepareStatement(createTableSQL)
        ) {
            st.execute();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }
    }

    // Quiero que el ID sea autoincremental
    public void createSequenceAndTrigger() throws DAOException {
        String createSequenceSQL = "DECLARE " +
                "   count_sequences NUMBER(1); " +
                "BEGIN " +
                "   SELECT COUNT(*) INTO count_sequences FROM user_sequences WHERE sequence_name = 'SEQ_VIDEOJOCS'; " +
                "   IF count_sequences = 0 THEN " +
                "       EXECUTE IMMEDIATE 'CREATE SEQUENCE SEQ_VIDEOJOCS START WITH 1 INCREMENT BY 1'; " +
                "   END IF; " +
                "END;";

        String createTriggerSQL = "DECLARE " +
                "   count_triggers NUMBER(1); " +
                "BEGIN " +
                "   SELECT COUNT(*) INTO count_triggers FROM user_triggers WHERE trigger_name = 'TRG_VIDEOJOCS'; " +
                "   IF count_triggers = 0 THEN " +
                "       EXECUTE IMMEDIATE 'CREATE TRIGGER TRG_VIDEOJOCS " +
                "           BEFORE INSERT ON VIDEOJOCS " +
                "           FOR EACH ROW " +
                "       BEGIN " +
                "           SELECT SEQ_VIDEOJOCS.NEXTVAL INTO :new.id FROM dual; " +
                "       END;'; " +
                "   END IF; " +
                "END;";

        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st1 = con.prepareStatement(createSequenceSQL);
             PreparedStatement st2 = con.prepareStatement(createTriggerSQL)
        ) {
            st1.execute();
            st2.execute();
        } catch (SQLException throwables) {
            throw new DAOException(1);
        }


    }
}