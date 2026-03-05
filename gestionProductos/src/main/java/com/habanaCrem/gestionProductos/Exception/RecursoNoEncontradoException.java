package com.habanaCrem.gestionProductos.Exception;

/**
 * Excepción lanzada cuando no se encuentra un registro dentro de le entidad
 * (ej: usuario no encontrado).
 */
public class RecursoNoEncontradoException extends RuntimeException{
    public RecursoNoEncontradoException(String mensaje){
        super(mensaje);
    }

}
