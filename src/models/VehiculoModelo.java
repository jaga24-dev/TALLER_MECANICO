package models;

/**
 * Modelo que representa un vehículo en el taller.
 * Cada vehículo tiene datos básicos (marca, modelo, año, placas)
 * y datos del taller (número de serie, falla reportada, imagen, estado).
 */
public class VehiculoModelo {

    // --- Datos básicos del vehículo ---
    private String id;
    private String marca;       // Ej: "Nissan"
    private String modelo;      // Ej: "Sentra"
    private int anio;           // Ej: 2018
    private String placas;      // Ej: "HPR-456-A"

    // --- Datos del taller (nuevos campos según la imagen) ---
    private String numeroSerie;     // Ej: "3N1AB7AP1JY327654"
    private String fallaReportada;  // Ej: "Falla en transmisión, no hace cambios de velocidad."
    private String imagenRuta;      // Ruta a la imagen del vehículo (puede ser null)
    private String estado;          // "Listo", "En Reparación", "En Espera"

    // Constructor vacío
    public VehiculoModelo() {
        this.estado = "En Espera"; // Estado por defecto
    }

    // Constructor con datos básicos (el que ya se usaba antes)
    public VehiculoModelo(String id, String marca, String modelo, int anio, String placas) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placas = placas;
        this.estado = "En Espera";
    }

    // Constructor completo con todos los campos
    public VehiculoModelo(String id, String marca, String modelo, int anio, String placas,
                          String numeroSerie, String fallaReportada, String imagenRuta, String estado) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placas = placas;
        this.numeroSerie = numeroSerie;
        this.fallaReportada = fallaReportada;
        this.imagenRuta = imagenRuta;
        this.estado = estado;
    }

    // --- Getters y Setters (para acceder y modificar cada campo) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getPlacas() { return placas; }
    public void setPlacas(String placas) { this.placas = placas; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getFallaReportada() { return fallaReportada; }
    public void setFallaReportada(String fallaReportada) { this.fallaReportada = fallaReportada; }

    public String getImagenRuta() { return imagenRuta; }
    public void setImagenRuta(String imagenRuta) { this.imagenRuta = imagenRuta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Devuelve un texto resumen del vehículo. Ej: "Nissan Sentra (2018)"
     */
    @Override
    public String toString() {
        return marca + " " + modelo + " (" + anio + ")";
    }
}
