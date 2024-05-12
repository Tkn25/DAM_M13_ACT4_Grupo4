package com.example.dam_m13_act4_grupo4.POJO;


public class Tratamiento {
    int id;
    Mascota mascota;
    String descripcion;
    String fecha;
    int finalizado;

    public Tratamiento(int id, Mascota mascota, String descripcion, String fecha, int finalizado) {
        this.id = id;
        this.mascota = mascota;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.finalizado = finalizado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(int finalizado) {
        this.finalizado = finalizado;
    }
}
