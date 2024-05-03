package com.example.dam_m13_act4_grupo4;

import java.util.Date;

public class Mascota {
    int id, idPropietario, idEspecie, idRaza, castrado, idGenero;
    String nombre,  microchip, fechaNacimiento;
    boolean enfermedad, baja;
    float peso;

    public Mascota(int id, int idPropietario, int idEspecie, int idRaza, String nombre, int idGenero, String microchip, int castrado, boolean enfermedad, boolean baja, float peso, String fechaNacimiento) {
        this.id = id;
        this.idPropietario = idPropietario;
        this.idEspecie = idEspecie;
        this.idRaza = idRaza;
        this.nombre = nombre;
        this.idGenero = idGenero;
        this.microchip = microchip;
        this.castrado = castrado;
        this.enfermedad = enfermedad;
        this.baja = baja;
        this.peso = peso;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    public int getIdEspecie() {
        return idEspecie;
    }

    public void setIdEspecie(int idEspecie) {
        this.idEspecie = idEspecie;
    }

    public int getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(int idRaza) {
        this.idRaza = idRaza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getidGenero() {
        return idGenero;
    }

    public void setidGenero(int idGenero) {
        this.idGenero= idGenero;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public int getCastrado() {
        return castrado;
    }

    public void setCastrado(int castrado) {
        this.castrado = castrado;
    }

    public boolean isEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(boolean enfermedad) {
        this.enfermedad = enfermedad;
    }

    public boolean isBaja() {
        return baja;
    }

    public void setBaja(boolean baja) {
        this.baja = baja;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
