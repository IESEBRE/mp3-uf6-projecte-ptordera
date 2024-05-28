package org.example.model.prop;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe Properties que gestiona la configuració de la base de dades.
 */
public class Properties {

    /**
     * Classe interna ConfiguracioBaseDades que conté la configuració de la base de dades.
     */
    public class ConfiguracioBaseDades {
        private String url;
        private String usuari;
        private String password;

        /**
         * Constructor de la classe ConfiguracioBaseDades.
         * Carrega les propietats de la base de dades des de l'arxiu database.properties.
         */
        public ConfiguracioBaseDades() {
            //try (InputStream input = getClass().getClassLoader().getResourceAsStream("./src/main/resources/database.properties")) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
                java.util.Properties prop = new java.util.Properties();

                if (input == null) {
                    System.out.println("No s'ha pogut trobar l'arxiu database.properties");
                    JOptionPane.showMessageDialog(null, "No s'ha pogut trobar l'arxiu database.properties", "Error", JOptionPane.ERROR_MESSAGE);
                    // Mostrarem un missatge d'error i sortirem del programa

                    return;
                }

                // Carrega el fitxer de propietats
                prop.load(input);

                // Obté les propietats i les assigna a les variables
                url = prop.getProperty("db.url");
                usuari = prop.getProperty("db.username");
                password = prop.getProperty("db.password");

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Mètode per obtenir l'URL de la base de dades.
         * @return L'URL de la base de dades.
         */
        public String getUrl() {
            return url;
        }

        /**
         * Mètode per obtenir el nom d'usuari de la base de dades.
         * @return El nom d'usuari de la base de dades.
         */
        public String getUsername() {
            return usuari;
        }

        /**
         * Mètode per obtenir la contrasenya de la base de dades.
         * @return La contrasenya de la base de dades.
         */
        public String getPassword() {
            return password;
        }
    }
}
