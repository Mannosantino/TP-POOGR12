public class Recurso {
    private String id;
    private String tipo;
    private String nombre;
    private int cantidad;

    public Recurso(String id, String tipo, String nombre, int cantidad) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}