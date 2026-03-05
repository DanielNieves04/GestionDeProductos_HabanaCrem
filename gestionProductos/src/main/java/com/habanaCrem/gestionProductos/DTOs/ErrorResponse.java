package com.habanaCrem.gestionProductos.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String mensaje;
    private LocalDateTime timestamp;
}
