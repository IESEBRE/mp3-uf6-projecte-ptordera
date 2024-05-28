package org.example.model.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe DAOException que s'estén d'Exception.
 * Aquesta classe s'utilitza per gestionar les excepcions específiques de DAO.
 */
public class DAOException extends Exception{

    // Mapa per emmagatzemar els missatges d'error
    private static final Map<Integer, String> missatges = new HashMap<>();
    //num i retorna string, el map
    static {
        missatges.put(2602, "Error al connectar a la BD!!");
        missatges.put(1, "Error al treballar amb la base de dades!!");
        missatges.put(3, "¡Error al inserir dades!");
        missatges.put(4, "¡Error al modificar dades!");
        missatges.put(5, "¡Error al eliminar dades!");
        missatges.put(57, "¡Falta omplir alguna dada!");
        missatges.put(386, "¡La regió ja existeix!");
        missatges.put(657, "El Titol ha de començar amb majúscula i no pot contenir caràcters especials");
        missatges.put(1065, "¡No s'ha seleccionat cap fila per a modificar!");
        missatges.put(1066, "¡No s'ha seleccionat cap fila per a eliminar!");

    }

    // Atribut per emmagatzemar el tipus d'error
    private int tipo;

    /**
     * Constructor de la classe DAOException.
     * @param tipo El tipus d'error.
     */
    public DAOException(int tipo){
        this.tipo=tipo;
    }

    /**
     * Sobreescriu el mètode getMessage de la classe Exception.
     * @return El missatge d'error corresponent al tipus d'error.
     */
    @Override
    public String getMessage(){
        return missatges.get(this.tipo); //el missatge del tipo
    }

    /**
     * Sobreescriu el mètode getMessage de la classe Exception.
     * @return El missatge d'error corresponent al tipus d'error.
     */
    public int getTipo() {
        return tipo;
    }
}
