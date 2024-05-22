package com.example.dam_m13_act4_grupo4.POJO;

import java.util.Date;

public class Seguimiento {
    private int id, idMascota;
    private String descripcion, imagen;
    private String fecha;
    private Mascota mascota;

    public Seguimiento(int id, int idMascota, String descripcion, String imagen, String fecha) {
        this.idMascota = idMascota;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.fecha = fecha;
    }

    public Seguimiento(int idMascota, Mascota mascota,String descripcion, String imagen, String fecha)
    {
        this.idMascota = idMascota;
        this.mascota = mascota;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }
}

