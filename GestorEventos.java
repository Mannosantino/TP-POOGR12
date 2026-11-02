import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorEventos {
    private List<EventoPapal> eventos = new ArrayList<>();
    private List<String> notificaciones = new ArrayList<>();
    private Map<String, Integer> personalGlobal = new HashMap<>();

    public GestorEventos() {
        personalGlobal.put("Seguridad", 1500);
        personalGlobal.put("Medicos", 300);
        personalGlobal.put("Catering", 500);
        personalGlobal.put("Organizacion", 250);
    }

    public void agregarNotificacion(String mensaje, boolean esEmergencia) {
        String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String prefijo = esEmergencia ? "🚨 URGENTE 🚨" : "ℹ️ SISTEMA";
        notificaciones.add("[" + hora + "] " + prefijo + ": " + mensaje);
    }

    public List<String> getNotificaciones() { return notificaciones; }

    private void validarHorario(LocalDateTime inicio, LocalDateTime fin, String idExcluido) {
        if (!inicio.isBefore(fin)) throw new IllegalArgumentException("Inicio debe ser anterior al fin.");
        for (EventoPapal e : eventos) {
            if (idExcluido != null && e.getId().equals(idExcluido)) continue;
            if (inicio.isBefore(e.getFin()) && fin.isAfter(e.getInicio())) {
                throw new IllegalArgumentException("Choque de horarios con el evento: " + e.getNombre());
            }
        }
    }

    public void crearEvento(String id, String nombre, LocalDateTime inicio, LocalDateTime fin, String ubicacion, String descripcion, int capacidadMaxima) {
        if (buscarEvento(id) != null) throw new IllegalArgumentException("El ID ya existe.");
        validarHorario(inicio, fin, null);
        eventos.add(new EventoPapal(id, nombre, inicio, fin, ubicacion, descripcion, capacidadMaxima));
        agregarNotificacion("Nuevo evento programado: " + nombre, false);
    }

    public void modificarEvento(String idEvento, String nombre, LocalDateTime inicio, LocalDateTime fin, String ubicacion, String descripcion, int capacidadMaxima) {
        EventoPapal e = buscarEvento(idEvento);
        if (e == null) throw new IllegalArgumentException("Evento no encontrado.");
        validarHorario(inicio, fin, idEvento);
        e.modificarEvento(nombre, inicio, fin, ubicacion, descripcion, capacidadMaxima);
        agregarNotificacion("Evento modificado: " + nombre, false);
    }

    public void eliminarEvento(String idEvento) {
        EventoPapal e = buscarEvento(idEvento);
        if (e == null) throw new IllegalArgumentException("Evento no encontrado.");
        for (Recurso r : e.getRecursos()) {
            personalGlobal.put(r.getTipo(), personalGlobal.getOrDefault(r.getTipo(), 0) + r.getCantidad());
        }
        eventos.remove(e);
        agregarNotificacion("Evento cancelado: " + e.getNombre(), false);
    }
    //La asignacion de personal, nuevas modificaciones, el cantidad <= 0 ya no permite negativos y el tipo == null hace que si se accede se tiene que indicar el tipo de personal.
    public void asignarPersonal(String idEvento, String tipo, int cantidad) {
        EventoPapal e = buscarEvento(idEvento);
        if (e == null) throw new IllegalArgumentException("Evento no encontrado.");

        if (cantidad <= 0 ) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        if(tipo == null || tipo.trim().isEmpty()) throw new IllegalArgumentException("Debe indicar el tipo de personal.");
        //Arregla el probelma que se generaba cuando se ponia Medico con tilde , decia que estaba asignado bien pero despues no se asignaba.
        String tipoLimpio = Normalizer.normalize(tipo.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String tipoFormateado = tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase();
        if (!personalGlobal.containsKey(tipoFormateado)) throw new IllegalArgumentException("Tipo de personal inexistente. Opciones: " + personalGlobal.keySet());

        int disponible = personalGlobal.getOrDefault(tipoFormateado, 0);
        if (disponible < cantidad) throw new IllegalArgumentException("Personal insuficiente. Disponible de " + tipoFormateado + ": " + disponible);

        personalGlobal.put(tipoFormateado, disponible - cantidad);
        e.agregarPersonal(tipoFormateado, cantidad);
    }

    public Map<String, Integer> getPersonalGlobal() { return personalGlobal; }
    public List<EventoPapal> listarEventos() { return eventos; }
    public EventoPapal buscarEvento(String id) {
        return eventos.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }
}

