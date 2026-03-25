package com.guillermo.idmtech.webapp.spring.service;

import com.guillermo.idmtech.webapp.spring.exception.BusinessException;
import com.guillermo.idmtech.webapp.spring.model.Cliente;
import com.guillermo.idmtech.webapp.spring.repository.ClienteRepository;
import com.guillermo.idmtech.webapp.spring.service.impl.ClienteServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceImplTest {
    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteServiceImpl service;

    private Cliente cliente;

    @BeforeEach
    void setup() {
        cliente = new Cliente(1L, "Juan", "juan@test.com");
    }

    //LISTAR
    @Test
    void listar_ok() {
        when(repository.findAll()).thenReturn(Flux.just(cliente));

        StepVerifier.create(service.listar())
                .expectNext(cliente)
                .verifyComplete();
    }

    //OBTENER POR ID
    @Test
    void obtenerPorId_ok() {
        when(repository.findById(1L)).thenReturn(Mono.just(cliente));

        StepVerifier.create(service.obtenerPorId(1L))
                .expectNext(cliente)
                .verifyComplete();
    }

    //OBTENER NO EXISTE
    @Test
    void obtenerPorId_noExiste() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.obtenerPorId(1L))
                .expectError(BusinessException.class)
                .verify();
    }

    //CREAR
    @Test
    void crear_ok() {
        when(repository.save(any(Cliente.class))).thenReturn(Mono.just(cliente));

        StepVerifier.create(service.crear(cliente))
                .expectNext(cliente)
                .verifyComplete();
    }

    //ACTUALIZAR
    @Test
    void actualizar_ok() {
        when(repository.findById(1L)).thenReturn(Mono.just(cliente));
        when(repository.save(any())).thenReturn(Mono.just(cliente));

        StepVerifier.create(service.actualizar(1L, cliente))
                .expectNext(cliente)
                .verifyComplete();
    }

    //ACTUALIZAR NO EXISTE
    @Test
    void actualizar_noExiste() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.actualizar(1L, cliente))
                .expectError(BusinessException.class)
                .verify();
    }

    //ELIMINAR
    @Test
    void eliminar_ok() {
        when(repository.findById(1L)).thenReturn(Mono.just(cliente));
        when(repository.delete(cliente)).thenReturn(Mono.empty());

        StepVerifier.create(service.eliminar(1L))
                .verifyComplete();
    }
}
