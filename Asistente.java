public class Asistente {
    private String perfil;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;

    public Asistente(String perfil, String dni, String nombre, String apellido, String email) {
        this.perfil = perfil;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getPerfil() { return perfil; }

    public String getZonaDeAcceso() {
        switch (perfil.toLowerCase()) {
            case "prensa": return "Sector de prensa acreditada";
            case "voluntario": return "Accesos de servicio y campo general";
            case "autoridad": return "Palco oficial";
            default: return "Campo general";
        }
    }

    public boolean recibeAvisoPrioritario() {
        return perfil.equalsIgnoreCase("autoridad");
    }
}