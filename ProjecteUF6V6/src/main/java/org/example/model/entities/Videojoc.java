package org.example.model.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Classe Videojoc que representa un videojoc amb les seves propietats i mètodes.
 */
public class Videojoc {

    private long id;
    private String titol;
    private double pegi;
    private boolean multijugador;

    private Collection<Equip> equips;


    public Videojoc(){}

    /**
     * Constructor de la classe Videojoc.
     * @param titol El títol del videojoc.
     * @param pegi La classificació PEGI del videojoc.
     * @param multijugador Si el videojoc és multijugador o no.
     * @param equips La col·lecció d'equips del videojoc.
     */
    public Videojoc(String titol, double pegi, boolean multijugador, Collection<Equip> equips) {
        this.titol = titol;
        this.pegi = pegi;
        this.multijugador=multijugador;
        this.equips=equips;
    }

    public Videojoc(long id, String titol) {
        this.id = id;
        this.titol = titol;
    }

    public Videojoc(long id, String titol, double pegi) {
        this.id = id;
        this.titol = titol;
        this.pegi = pegi;
    }

    public Videojoc(long id, String titol, double pegi, TreeSet<Equip> equips) {
        this.id = id;
        this.titol = titol;
        this.pegi = pegi;
        this.equips = equips;
    }

    public Videojoc(long id, String titol, double pegi, boolean multijugador, TreeSet<Equip> equips) {
        this(id,titol,pegi,equips);
        this.multijugador = multijugador;
    }

    public Collection<Videojoc.Equip> getMatricules() {
        return equips;
    }

    private void setMatricules(Collection<Equip> matricules) {
        this.equips = equips;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public double getPegi() {
        return pegi;
    }

    public void setPegi(double pegi) {
        this.pegi = pegi;
    }

    public boolean isMultijugador() {
        return multijugador;
    }

    public void setMultijugador(boolean multijugador) {
        this.multijugador = multijugador;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Classe interna Equip que representa un equip d'un videojoc.
     */
    public static class Equip implements Comparable<Equip>{

        @Override
        public int compareTo(Equip o) {
            return this.regio.compareTo(o.getRegio());
        }

        /**
         * Enumeració Regio que representa les diferents regions d'un equip.
         */
        public static enum Regio {
            AF("África"), AN("Antártida"),
            AS("Asia"), EU("Europa"),
            NA("América del Nord"), OC("Oceania"),
            SA("América del Sud");
            private String nom;

            private Regio(String nom) {
                this.nom = nom;
            }

            public String getNom() {
                return nom;
            }

            @Override
            public String toString() {
                return this.name()+" - " +nom;
            }
        }

        private Equip.Regio regio;
        private String nom;
        private long id_equip;
        private long videojoc_id;

        /**
         * Constructor de la classe Equip.
         * @param regio La regió de l'equip.
         * @param nom El nom de l'equip.
         * @param id_equip L'ID de l'equip.
         * @param videojoc_id L'ID del videojoc al qual pertany l'equip.
         */
        public Equip(Equip.Regio regio, String nom, long id_equip, long videojoc_id) {
            this.regio = regio;
            this.nom = nom;
            this.id_equip = id_equip;
            this.videojoc_id = videojoc_id;
        }

        public Equip.Regio getRegio() {
            return regio;
        }

        public void setRegio(Equip.Regio regio) {
            this.regio = regio;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public long getId_equip() {
            return id_equip;
        }

        public void setId_equip(long id_equip) {
            this.id_equip = id_equip;
        }

        public long getVideojoc_id() {
            return videojoc_id;
        }

        public void setVideojoc_id(long videojoc_id) {
            this.videojoc_id = videojoc_id;
        }



    }

    /**
     * Mètode per obtenir un equip del videojoc segons el seu ID.
     * @param id_equip L'ID de l'equip a obtenir.
     * @return L'equip obtingut o null si no es troba cap equip amb aquest ID.
     */
    public Equip getEquipById(long id_equip) {
        for (Equip equip : this.getMatricules()) {
            if (equip.getId_equip() == id_equip) {
                return equip;
            }
        }
        return null; // return null if no Equip with the given id_equip is found
    }

    // Fem que es queden les dades a la taula equips
    public void addEquip(Equip equip) {
        if (this.equips == null) {
            this.equips = new HashSet<>();
        }
        this.equips.add(equip);
    }

    public boolean removeEquip(Equip equip) {
        return this.getMatricules().remove(equip);
    }

}

