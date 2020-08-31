package com.example.smartsound.model;

public class Persona {
    //private String pid;
    private String nombre;
    private String apellidos;
    private String correo;
    private String usuario;
    private String clave;
    private String telefono;
    private String contrasenaDispositivo;

    public Persona() {
    }

    /*public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }*/

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String puesto) {
        this.telefono = puesto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenaDispositivo() {
        return contrasenaDispositivo;
    }

    public void setContrasenaDispositivo(String contrasenaDispositivo) {
        this.contrasenaDispositivo = contrasenaDispositivo;
    }

    @Override
    public String toString() {
        return usuario;
    }

}
