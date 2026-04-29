package models;

/**
 * Modelo de usuario para autenticación.
 * Contiene las credenciales válidas y la lógica de validación.
 */
public class UsuarioModelo {

    private String usuarioCorrecto = "admin";
    private String passwordCorrecto = "1234";

    public boolean validarCredenciales(String usuario, String password) {
        return usuarioCorrecto.equals(usuario) && passwordCorrecto.equals(password);
    }

    public String getUsuarioCorrecto() {
        return usuarioCorrecto;
    }

    public void setUsuarioCorrecto(String usuario) {
        this.usuarioCorrecto = usuario;
    }

    public void setPasswordCorrecto(String password) {
        this.passwordCorrecto = password;
    }
}
