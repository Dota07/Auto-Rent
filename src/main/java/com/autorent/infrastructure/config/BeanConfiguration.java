package com.autorent.infrastructure.config;

import com.autorent.application.port.output.AlquilerRepositoryPort;
import com.autorent.application.port.output.AutoRepositoryPort;
import com.autorent.application.port.output.ClienteRepositoryPort;
import com.autorent.application.port.input.AlquilerService;
import com.autorent.application.port.input.AutoService;
import com.autorent.application.port.input.ClienteService;
import com.autorent.application.usecase.AlquilerUseCase;
import com.autorent.application.usecase.AutoUseCase;
import com.autorent.application.usecase.ClienteUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public AlquilerService alquilerService(AlquilerRepositoryPort alquilerRepository,
            AutoRepositoryPort autoRepository, ClienteRepositoryPort clienteRepository) {
        return new AlquilerUseCase(alquilerRepository, autoRepository, clienteRepository);
    }

    @Bean
    public AutoService autoService(AutoRepositoryPort autoRepository) {
        return new AutoUseCase(autoRepository);
    }

    @Bean
    public ClienteService clienteService(ClienteRepositoryPort clienteRepository,
            AlquilerRepositoryPort alquilerRepository) {
        return new ClienteUseCase(clienteRepository, alquilerRepository);
    }
}
