package POJO;


public class Cita {
    int id;
    String motivo;
    Mascota mascota;
    String fecha;

    public Cita() {
    }

    public Cita(int id, String motivo, Mascota mascota, String fecha) {
        this.id = id;
        this.motivo = motivo;
        this.mascota = mascota;
        this.fecha = fecha;
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

}
