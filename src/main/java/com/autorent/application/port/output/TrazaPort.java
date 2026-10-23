package com.autorent.application.port.output;

import com.autorent.application.PasoTraza;
import java.util.List;

// Guarda los pasos de una sola petición HTTP para explicarlos en pantalla.
public interface TrazaPort {
    void registrar(String nodo, String detalle);
    List<PasoTraza> obtenerPasos();
}
