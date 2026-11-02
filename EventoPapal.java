import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventoPapal {
    private String id;
    private String nombre;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String ubicacion;
    private String descripcion;
    private int capacidadMaxima;

    private List<Asistente> asistentes = new ArrayList<>();
    private List<Recurso> recursos = new ArrayList<>();
    private List<Tarea> tareas = new ArrayList<>();

    private int asistenciaReal;
    private LocalDateTime finReal;
    private int puntaje;
    private String comentario;
    private boolean realizado = false;

    public EventoPapal(String id, String nombre, LocalDateTime inicio, LocalDateTime fin, String ubicacion, String descripcion, int capacidadMaxima) {
        this.id = id; this.nombre = nombre; this.inicio = inicio; this.fin = fin;
        this.ubicacion = ubicacion; this.descripcion = descripcion; this.capacidadMaxima = capacidadMaxima;
    }

    public void modificarEvento(String nombre, LocalDateTime inicio, LocalDateTime fin, String ubicacion, String descripcion, int capacidadMaxima) {
        if (capacidadMaxima < asistentes.size()) {
            throw new IllegalArgumentException("Capacidad menor a inscriptos actuales.");
        }
        this.nombre = nombre; this.inicio = inicio; this.fin = fin;
        this.ubicacion = ubicacion; this.descripcion = descripcion; this.capacidadMaxima = capacidadMaxima;
    }

    public void inscribirAsistente(Asistente asistente) {
        if (realizado || inicio.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("El evento ya finalizó o está en curso.");
        if (cuposDisponibles() <= 0) throw new IllegalArgumentException("No quedan cupos disponibles.");
        for (Asistente a : asistentes) {
            if (a.getDni().equals(asistente.getDni())) throw new IllegalArgumentException("El DNI ya está inscripto.");
        }
        asistentes.add(asistente);
    }

    public void agregarTarea(String descripcion, String responsable, LocalTime horaInicio, LocalTime horaFin) {
        tareas.add(new Tarea(descripcion, responsable, horaInicio, horaFin));
        Collections.sort(tareas);
    }

    public void agregarPersonal(String tipo, int cantidad) {
        for (Recurso r : recursos) {
            if (r.getTipo().equalsIgnoreCase(tipo)) {
                r.setCantidad(r.getCantidad() + cantidad);
                return;
            }
        }
        recursos.add(new Recurso("R-" + tipo, tipo, tipo, cantidad));
    }

    public void registrarResultado(int asistenciaReal, LocalDateTime finReal, int puntaje, String comentario) {
        if (puntaje < 1 || puntaje > 5) throw new IllegalArgumentException("Puntuación entre 1 y 5.");
        if (asistenciaReal < 0) throw new IllegalArgumentException("Asistencia negativa no permitida.");
        this.asistenciaReal = asistenciaReal; this.finReal = finReal; this.puntaje = puntaje;
        this.comentario = comentario; this.realizado = true;
    }

    public String generarComparacionReal() {
        if (!realizado) return "Sin resultados cargados.";
        long minPrev = ChronoUnit.MINUTES.between(inicio, fin);
        long minReales = ChronoUnit.MINUTES.between(inicio, finReal);
        return String.format("%d de %d inscriptos; %d min previstos y %d reales. Puntos: %d/5.",
                asistenciaReal, asistentes.size(), minPrev, minReales, puntaje);
    }

    public int cuposDisponibles() { return capacidadMaxima - asistentes.size(); }
    public double getOcupacion() { return capacidadMaxima == 0 ? 0 : ((double) asistentes.size() / capacidadMaxima) * 100; }

    public List<Tarea> getTareas() { return Collections.unmodifiableList(tareas); }
    public List<Asistente> getAsistentes() { return Collections.unmodifiableList(asistentes); }
    public List<Recurso> getRecursos() { return Collections.unmodifiableList(recursos); }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public String getUbicacion() { return ubicacion; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
}