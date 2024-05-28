-- Poseu el codi dels procediments/funcions emmagatzemats, triggers, ..., usats al projecte

-- Crear la taula videojocs
create or replace PROCEDURE crear_taula_videojocs AUTHID CURRENT_USER IS
BEGIN
    EXECUTE IMMEDIATE 'CREATE TABLE VIDEOJOCS (
        id NUMBER(10) PRIMARY KEY,
        titol VARCHAR2(100),
        pegi NUMBER(10, 2),
        multijugador CHAR(1 CHAR)
    )';
    DBMS_OUTPUT.PUT_LINE('Taula VIDEOJOCS creada correctament.');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error al crear la taula VIDEOJOCS: ' || SQLERRM);
END;

-- Crear la taula equips
CREATE OR REPLACE PROCEDURE crear_taula_equips AUTHID CURRENT_USER IS
BEGIN
    EXECUTE IMMEDIATE 'CREATE TABLE EQUIPS (
        id_equip NUMBER(10) PRIMARY KEY,
        nom VARCHAR2(100),
        regio VARCHAR2(50),
        videojoc_id NUMBER(10),
        CONSTRAINT fk_videojoc_id FOREIGN KEY (videojoc_id)
            REFERENCES VIDEOJOCS(id),
        CONSTRAINT chk_regio CHECK (regio IN (''AF'', ''AN'', ''AS'', ''EU'', ''NA'', ''OC'', ''SA'')),
        CONSTRAINT uq_regio_videojoc UNIQUE (regio, videojoc_id)
    )';
    DBMS_OUTPUT.PUT_LINE('Taula EQUIPS creada correctament.');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error al crear la taula EQUIPS: ' || SQLERRM);
END;
/

-- Crear la taula avgpegi
CREATE OR REPLACE PROCEDURE crear_taula_avgpegi AUTHID CURRENT_USER IS
BEGIN
    -- Crear la tabla AVGPEGI
    EXECUTE IMMEDIATE '
        CREATE TABLE AVGPEGI (
            id NUMBER(10) PRIMARY KEY,
            avgPegi NUMBER(10, 2)
        )';
    
    -- Insertar una fila inicial con id=1 y avgPegi=NULL
    EXECUTE IMMEDIATE 'INSERT INTO AVGPEGI (id, avgPegi) VALUES (1, NULL)';
    
    -- Mostrar mensaje de éxito
    DBMS_OUTPUT.PUT_LINE('Taula AVGPEGI creada correctament.');
EXCEPTION
    WHEN OTHERS THEN
        -- Mostrar mensaje de error si ocurre alguna excepción
        DBMS_OUTPUT.PUT_LINE('Error al crear la taula AVGPEGI: ' || SQLERRM);
END;
/



-- Funció emmagatzemada --> Procediment :(
CREATE OR REPLACE PROCEDURE update_avgPegi IS
    v_pegisum NUMBER;
    v_numRows NUMBER;
BEGIN
    v_pegisum := 0;
    v_numRows := 0;
    
    -- Obtenir la suma i el nombre de files
    SELECT NVL(SUM(pegi), 0), COUNT(*)
    INTO v_pegisum, v_numRows
    FROM VIDEOJOCS;

    -- Verificar que el nombre de files no sigui 0 per evitar la divisió entre 0
    IF v_numRows > 0 THEN
        -- Actualitzar la taula AVGPEGI
        UPDATE AVGPEGI
        SET avgPegi = v_pegisum / v_numRows;
        
        COMMIT;
    ELSE
        -- Si no es troba cap fila a la taula VIDEOJOCS, establir avgPegi a 0
        UPDATE AVGPEGI
        SET avgPegi = 0;
        
        COMMIT;
        
        -- Llançar un missatge informatiu en lloc d'una excepció
        DBMS_OUTPUT.PUT_LINE('No s´han trobat files a la taula VIDEOJOCS.');
    END IF;
EXCEPTION
    -- En cas d'altres errors, fer una transacció de retorn i llançar l'error
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_avgPegi;
/

-- Funció emmagatzemada
CREATE OR REPLACE FUNCTION calculate_avgPegi RETURN NUMBER IS
    v_pegisum NUMBER := 0;
    v_numRows NUMBER := 0;
    v_avgPegi NUMBER;
BEGIN
    -- Obtenir la suma i el nombre de files
    SELECT NVL(SUM(pegi), 0), COUNT(*)
    INTO v_pegisum, v_numRows
    FROM VIDEOJOCS;

    -- Verificar que el nombre de files no sigui 0 per evitar la divisió entre 0
    IF v_numRows > 0 THEN
        -- Calcular la mitjana de PEGI
        v_avgPegi := v_pegisum / v_numRows;
    ELSE
        -- Si no es troba cap fila a la taula VIDEOJOCS, establir avgPegi a 0
        v_avgPegi := 0;
        
        -- Llançar un missatge informatiu en lloc d'una excepció
        DBMS_OUTPUT.PUT_LINE('No s´han trobat files a la taula VIDEOJOCS.');
    END IF;
    
    RETURN v_avgPegi;
EXCEPTION
    -- En cas d'altres errors, llançar els errors
    WHEN OTHERS THEN
        RAISE;
END calculate_avgPegi;
/



