package com.nariz.narizapp.model;

/**
 * Created by ACsatillo on 04/09/2015.
 */
public class Peque extends CardObject {


    private String paterno = "";
    private String materno = "";
    private String telefono = "";
    private String cumplea = "";

    public Peque() {}

    public Peque(String nombre, String paterno, String materno, String telefono, String cumplea, String comentario, String imagen) {
        super(nombre, comentario, imagen);
        this.paterno = paterno;
        this.materno = materno;
        this.telefono = telefono;
        this.cumplea = cumplea;
    }

    public Peque(String nombre, String paterno, String materno, String telefono, String cumplea, String comentario) {
        super(nombre, comentario);
        this.paterno = paterno;
        this.materno = materno;
        this.telefono = telefono;
        this.cumplea = cumplea;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCumplea() {
        return cumplea;
    }

    public void setCumplea(String cumplea) {
        this.cumplea = cumplea;
    }

    @Override
    public String toString() {
        return "Peque{" +
                "paterno='" + paterno + '\'' +
                ", materno='" + materno + '\'' +
                ", telefono='" + telefono + '\'' +
                ", cumplea='" + cumplea + '\'' +
                "} " + super.toString();
    }
}
