package com.example.dam_m13_act4_grupo4;

public class Dueno {
    private int idCliente;
    private int tipo;
    private String usuario;
    private String pass;
    private String DNI;
    private String nombre;
    private String telefono;
    private String direccion;

    public Dueno(int idCliente) {
        this.idCliente = idCliente;
    }

    public Dueno(int idCliente, String nombre) {
        this.idCliente = idCliente;
        this.nombre = nombre;
    }

    public Dueno(int idCliente, int tipo, String usuario, String pass, String DNI, String nombre, String telefono, String direccion) {
        this.idCliente = idCliente;
        this.tipo = tipo;
        this.usuario = usuario;
        this.pass = pass;
        this.DNI = DNI;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
