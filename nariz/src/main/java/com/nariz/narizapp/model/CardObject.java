package com.nariz.narizapp.model;

/**
 * Created by ACsatillo on 14/09/2015.
 */
public class CardObject {

    private String id = "";
    private String nombre = "";
    private String comentario = "";
    private String imagen = "";

    public CardObject(){}

    public CardObject(String id, String nom, String com, String img){
        this.id = id;
        this.nombre = nom;
        this.comentario = com;
        this.imagen = img;
    }

    public CardObject(String nom, String com, String img){
        this.nombre = nom;
        this.comentario = com;
        this.imagen = img;
    }

    public CardObject(String nom, String com){
        this.nombre = nom;
        this.comentario = com;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "CardObject{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", comentario='" + comentario + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}
