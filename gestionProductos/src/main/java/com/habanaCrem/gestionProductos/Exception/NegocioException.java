package com.habanaCrem.gestionProductos.Exception;

/**
 * Excepción lanzada cuando una operación viola una regla
 * de negocio del sistema (ej: stock insuficiente).
 */
public class NegocioException extends RuntimeException{
    public NegocioException(String mensaje){
        super(mensaje);
    }
}
