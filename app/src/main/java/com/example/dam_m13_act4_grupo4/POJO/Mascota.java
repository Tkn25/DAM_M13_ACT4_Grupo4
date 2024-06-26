package com.example.dam_m13_act4_grupo4.POJO;

import java.util.Date;

public class Mascota {
    private int id, idPropietario, idEspecie, castrado, idGenero;
    private String nombre,  microchip, fechaNacimiento, raza;
    private boolean enfermedad, baja;
    private float peso;
    private String dni;

    public Mascota(int id, int idPropietario, int idEspecie, String raza, String nombre, int idGenero, String microchip, int castrado, boolean enfermedad, boolean baja, float peso, String fechaNacimiento) {
        this.id = id;
        this.idPropietario = idPropietario;
        this.idEspecie = idEspecie;
        this.raza = raza;
        this.nombre = nombre;
        this.idGenero = idGenero;
        this.microchip = microchip;
        this.castrado = castrado;
        this.enfermedad = enfermedad;
        this.baja = baja;
        this.peso = peso;
        this.fechaNacimiento = fechaNacimiento;
    }
    public Mascota(int id, int idPropietario, int idEspecie, String raza, String nombre, int idGenero, String microchip, int castrado, boolean enfermedad, boolean baja, float peso, String fechaNacimiento,String dni) {
        this.id = id;
        this.idPropietario = idPropietario;
        this.idEspecie = idEspecie;
        this.raza = raza;
        this.nombre = nombre;
        this.idGenero = idGenero;
        this.microchip = microchip;
        this.castrado = castrado;
        this.enfermedad = enfermedad;
        this.baja = baja;
        this.peso = peso;
        this.fechaNacimiento = fechaNacimiento;
        this.dni = dni;
    }
    public int getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(int idGenero) {
        this.idGenero = idGenero;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Mascota(int id)
    {
        this.id = id;
    }

    public Mascota(String nombre)
    {
        this.nombre = nombre;
    }

    public Mascota(int id, String nombre)
    {
        this.id = id;
        this.nombre = nombre;
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

    public String getRaza() {
        return raza;
    }

    public void setIdRaza(String Raza) {
        this.raza = raza;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
