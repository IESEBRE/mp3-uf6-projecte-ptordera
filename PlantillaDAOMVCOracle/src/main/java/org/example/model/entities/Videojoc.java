package org.example.model.entities;

import java.util.Collection;
import java.util.TreeSet;

public class Videojoc {

    private Long id;
    private String titol;
    private double pegi;
    private boolean multijugador;

    private Collection<Equip> equips;


    public Videojoc(){}

    public Videojoc(String titol, double pegi, boolean multijugador, Collection<Equip> equips) {
        this.titol = titol;
        this.pegi = pegi;
        this.multijugador=multijugador;
        this.equips=equips;
    }

    public Videojoc(Long id, String titol) {
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

    public Collection<Videojoc.Equip> getMatricules() {
        return equips;
    }

    private void setMatricules(Collection<Equip> matricules) {
        this.equips = equips;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String nom) {
        this.titol = titol;
    }

    public double getPegi() {
        return pegi;
    }

    public void setPegi(double pes) {
        this.pegi = pegi;
    }

    public boolean isMultijugador() {
        return multijugador;
    }

    public void setMultijugador(boolean multijugador) {
        this.multijugador = multijugador;
    }

    public static class Equip implements Comparable<Equip>{

        @Override
        public int compareTo(Equip o) {
            return this.regio.compareTo(o.getRegio());
        }

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

        public Equip(Equip.Regio regio, String nom) {
            this.regio = regio;
            this.nom = nom;
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


    }


}

