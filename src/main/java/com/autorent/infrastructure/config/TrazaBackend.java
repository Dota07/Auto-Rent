package com.autorent.infrastructure.config;

import com.autorent.application.PasoTraza;
import com.autorent.application.port.output.TrazaPort;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

// Spring crea una traza independiente para cada petición (sin mezclar usuarios).
@Component
@RequestScope
public class TrazaBackend implements TrazaPort {
    private static final Logger logger = LoggerFactory.getLogger(TrazaBackend.class);
    private final List<PasoTraza> pasos = new ArrayList<>();

    @Override
    public void registrar(String nodo, String detalle) {
        pasos.add(new PasoTraza(nodo, detalle));
        logger.info("[FLUJO BACKEND] {}", detalle);
    }

    @Override
    public List<PasoTraza> obtenerPasos() {
        return pasos;
    }
}
