import java.time.LocalTime;

public class Tarea implements Comparable<Tarea> {
    private String descripcion;
    private String responsable;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Tarea(String descripcion, String responsable, LocalTime horaInicio, LocalTime horaFin) {
        this.descripcion = descripcion;
        this.responsable = responsable;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public boolean seSolapaCon(Tarea otra) {
        return horaInicio.isBefore(otra.horaFin) && horaFin.isAfter(otra.horaInicio);
    }

    @Override
    public int compareTo(Tarea otra) {
        return this.horaInicio.compareTo(otra.horaInicio);
    }

    public String getResponsable() { return responsable; }
    public String getDescripcion() { return descripcion; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
}