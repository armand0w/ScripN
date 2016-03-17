package com.nariz.narizapp.model;

/**
 * Created by ACsatillo on 15/09/2015.
 */
public class CardInfo {

    //private String id = "";
    private String titulo = "";
    private String info = "";
    private int imagen = 0;

    public CardInfo(){}

    public CardInfo(String id, String tit, String inf, int img){
        //this.id = id;
        this.titulo = tit;
        this.info = inf;
        this.imagen = img;
    }

    public CardInfo(String tit, String inf, int img){
        this.titulo = tit;
        this.info = inf;
        this.imagen = img;
    }

    /*public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "CardInfo{" +
                //"id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", info='" + info + '\'' +
                ", imagen=" + imagen +
                '}';
    }
}
