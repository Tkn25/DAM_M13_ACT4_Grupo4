package com.example.dam_m13_act4_grupo4.POJO;


public class Cita {
    private int id;
    private String motivo;
    private Mascota mascota;
    private String fecha;
    private String nombreMascota;

    public Cita() {
    }

    public Cita(int id, String motivo, Mascota mascota, String fecha) {
        this.id = id;
        this.motivo = motivo;
        this.mascota = mascota;
        this.fecha = fecha;
    }

    //Constructor para las citas de los clientes
    public Cita(String motivo, String fecha, String nombreMascota) {
        this.motivo = motivo;
        this.fecha = fecha;
        this.nombreMascota = nombreMascota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }
}
