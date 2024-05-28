package org.example.model.daos;


import org.example.model.exceptions.DAOException;

import java.util.List;

/**
 * Interfície DAO que defineix els mètodes necessaris per interactuar amb la base de dades.
 * @param <T> El tipus d'objecte que aquest DAO gestiona.
 */
public interface DAO <T>{

    /**
     * Mètode per obtenir un objecte de la base de dades segons el seu ID.
     * @param id L'ID de l'objecte a obtenir.
     * @return L'objecte obtingut de la base de dades.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    T get(Long id) throws DAOException;

    /**
     * Mètode per obtenir tots els objectes de la base de dades.
     * @return Una llista amb tots els objectes de la base de dades.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    List<T> getAll() throws DAOException;

    /**
     * Mètode per inserir un nou objecte a la base de dades.
     * @param obj L'objecte a inserir.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    void insert(T obj) throws DAOException;

    /**
     * Mètode per actualitzar un objecte existent a la base de dades.
     * @param obj L'objecte a actualitzar.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    void update(T obj) throws DAOException;

    /**
     * Mètode per eliminar un objecte de la base de dades.
     * @param obj L'objecte a eliminar.
     * @throws DAOException Si es produeix una excepció en l'accés a les dades.
     */
    void delete(T obj) throws DAOException;

    //Tots els mètodes necessaris per interactuar en la BD

}
