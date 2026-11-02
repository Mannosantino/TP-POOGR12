import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends JFrame {
    private GestorEventos gestor;
    private boolean esAdmin;
    private JTextArea pantallaText;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Main(GestorEventos gestor, boolean esAdmin) {
        this.gestor = gestor;
        this.esAdmin = esAdmin;

        setTitle("Visita Papal - León XIV (" + (esAdmin ? "Administrador" : "Visitante") + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        pantallaText = new JTextArea();
        pantallaText.setEditable(false);
        pantallaText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(pantallaText), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 5, 5));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnVerEventos = new JButton("Ver Eventos y Cupos");
        JButton btnInscribir = new JButton("Inscribirse a Evento");
        JButton btnNotificaciones = new JButton("Ver Notificaciones");

        panelBotones.add(btnVerEventos);
        panelBotones.add(btnInscribir);
        panelBotones.add(btnNotificaciones);

        if (esAdmin) {
            JButton btnCrear = new JButton("Crear Evento");
            JButton btnEliminar = new JButton("Eliminar Evento");
            JButton btnAsignar = new JButton("Asignar Personal");
            JButton btnEmergencia = new JButton("EMITIR ALERTA (Urgente)");
            btnEmergencia.setForeground(Color.RED);

            panelBotones.add(btnCrear);
            panelBotones.add(btnEliminar);
            panelBotones.add(btnAsignar);
            panelBotones.add(btnEmergencia);

            btnCrear.addActionListener(e -> crearEventoGUI());
            btnEliminar.addActionListener(e -> eliminarEventoGUI());
            btnAsignar.addActionListener(e -> asignarPersonalGUI());
            btnEmergencia.addActionListener(e -> alertaGUI());
        }

        // --- NUEVO BOTÓN: Cerrar Sesión ---
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.WEST);

        btnVerEventos.addActionListener(e -> mostrarEventos());
        btnInscribir.addActionListener(e -> inscribirGUI());
        btnNotificaciones.addActionListener(e -> mostrarNotificaciones());

        // --- ACCIÓN: Cerrar Sesión ---
        btnCerrarSesion.addActionListener(e -> {
            this.dispose(); // Cierra la ventana principal actual
            mostrarLogin(gestor); // Abre de nuevo el cuadro de inicio de sesión
        });

        mostrarNotificaciones();
    }

    private void mostrarEventos() {
        StringBuilder sb = new StringBuilder("=== AGENDA DE EVENTOS ===\n\n");
        for (EventoPapal e : gestor.listarEventos()) {
            sb.append(String.format("[%s] %s\nUbicación: %s\nHorario: %s a %s\nCupos disponibles: %d\n",
                    e.getId(), e.getNombre(), e.getUbicacion(), e.getInicio().format(fmt), e.getFin().format(fmt), e.cuposDisponibles()));

            if (!e.getRecursos().isEmpty()) {
                sb.append("Personal asignado:\n");
                for (Recurso r : e.getRecursos()) {
                    sb.append("- ").append(r.getTipo()).append(": ").append(r.getCantidad()).append("\n");
                }
            }
            sb.append("-----------------------------------\n");
        }
        pantallaText.setText(sb.toString());
    }

    private void mostrarNotificaciones() {
        StringBuilder sb = new StringBuilder("=== NOTIFICACIONES DEL SISTEMA ===\n\n");
        for (String n : gestor.getNotificaciones()) {
            sb.append(n).append("\n");
        }
        pantallaText.setText(sb.toString());
    }

    private void crearEventoGUI() {
        try {
            String id = JOptionPane.showInputDialog(this, "ID del Evento:");
            if (id == null) return;
            String nombre = JOptionPane.showInputDialog(this, "Nombre:");
            String inicioStr = JOptionPane.showInputDialog(this, "Inicio (dd/MM/yyyy HH:mm):");
            String finStr = JOptionPane.showInputDialog(this, "Fin (dd/MM/yyyy HH:mm):");
            String ubi = JOptionPane.showInputDialog(this, "Ubicación:");
            String cap = JOptionPane.showInputDialog(this, "Capacidad Máxima:");

            if (inicioStr != null) {
                gestor.crearEvento(id, nombre, LocalDateTime.parse(inicioStr, fmt), LocalDateTime.parse(finStr, fmt), ubi, "", Integer.parseInt(cap));
                JOptionPane.showMessageDialog(this, "Evento creado exitosamente.");
                mostrarEventos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error al crear", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEventoGUI() {
        try {
            String id = JOptionPane.showInputDialog(this, "Ingrese el ID del evento a cancelar:");
            if (id != null) {
                gestor.eliminarEvento(id);
                JOptionPane.showMessageDialog(this, "Evento eliminado.");
                mostrarEventos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inscribirGUI() {
        try {
            String id = JOptionPane.showInputDialog(this, "ID del Evento:");
            if (id == null) return;
            EventoPapal e = gestor.buscarEvento(id);
            if (e == null) throw new IllegalArgumentException("Evento no encontrado.");

            String perfil = JOptionPane.showInputDialog(this, "Perfil (Peregrino, Prensa, Voluntario, Autoridad):");
            String dni = JOptionPane.showInputDialog(this, "DNI:");
            String nombre = JOptionPane.showInputDialog(this, "Nombre:");

            if (perfil != null && dni != null) {
                e.inscribirAsistente(new Asistente(perfil, dni, nombre, "", ""));
                JOptionPane.showMessageDialog(this, "¡Inscripción exitosa! Zona: " + e.getAsistentes().get(e.getAsistentes().size()-1).getZonaDeAcceso());
                mostrarEventos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignarPersonalGUI() {
        try {
            String id = JOptionPane.showInputDialog(this, "ID del Evento:");
            if (id == null) return;
            String tipo = JOptionPane.showInputDialog(this, "Tipo (Seguridad, Medicos, Catering, Organizacion):");
            String cant = JOptionPane.showInputDialog(this, "Cantidad:");

            if (tipo != null && cant != null) {
                gestor.asignarPersonal(id, tipo, Integer.parseInt(cant));
                JOptionPane.showMessageDialog(this, "Personal asignado.");
                mostrarEventos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alertaGUI() {
        String msg = JOptionPane.showInputDialog(this, "Mensaje de emergencia:");
        if (msg != null && !msg.trim().isEmpty()) {
            gestor.agregarNotificacion(msg, true);
            mostrarNotificaciones();
        }
    }

    // --- NUEVO MÉTODO: Extrae la lógica de inicio de sesión ---
    public static void mostrarLogin(GestorEventos gestor) {
        String input = JOptionPane.showInputDialog(null, "Ingrese usuario ('admin' para organizador, deje vacío para visitante):", "Inicio de Sesión", JOptionPane.QUESTION_MESSAGE);

        if (input == null) System.exit(0); // Si presiona "Cancelar" o la X, se cierra la app
        boolean esAdmin = input.trim().equalsIgnoreCase("admin");

        SwingUtilities.invokeLater(() -> {
            new Main(gestor, esAdmin).setVisible(true);
        });
    }

    public static void main(String[] args) {
        GestorEventos gestor = new GestorEventos();

        // Datos de prueba iniciales
        gestor.crearEvento("E01", "Misa Luna Park", LocalDateTime.of(2027, 5, 10, 10, 0), LocalDateTime.of(2027, 5, 10, 13, 0), "Luna Park", "Misa", 10000);

        // Llama al método de login por primera vez
        mostrarLogin(gestor);
    }
}