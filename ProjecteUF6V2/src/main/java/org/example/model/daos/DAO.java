package org.example.model.daos;


import org.example.model.exceptions.DAOException;

import java.util.List;

public interface DAO <T>{

    T get(Long id) throws DAOException;

    List<T> getAll() throws DAOException;

    void insert(T obj) throws DAOException;

    void update(T obj) throws DAOException;

    void delete(T obj) throws DAOException;

    //Tots els mètodes necessaris per interactuar en la BD

}
