package com.guillermo.idmtech.webapp.spring.service.impl;

import com.guillermo.idmtech.webapp.spring.exception.RecursoNoEncontradoException;
import com.guillermo.idmtech.webapp.spring.exception.ValidacionException;
import com.guillermo.idmtech.webapp.spring.model.Cliente;
import com.guillermo.idmtech.webapp.spring.repository.ClienteRepository;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {
    
    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteServiceImpl service;

    private Cliente cliente;
    private final Long CLIENTE_ID = 1L;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(CLIENTE_ID, "Juan Pérez", "juan@example.com");
    }

    @SuppressWarnings("null")
    @Test
    void crear_ClienteValido_RetornaCliente() {
        when(repository.save(any(Cliente.class))).thenReturn(Mono.just(cliente));;

        StepVerifier.create(service.crear(cliente))
                .expectNext(cliente)
                .verifyComplete();

        verify(repository).save(cliente);
    }

    @SuppressWarnings("null")
    @Test
    void crear_ClienteConNombreVacio_LanzaValidacionException() {
        Cliente clienteInvalido = new Cliente(null, "", "email@valido.com");

        StepVerifier.create(service.crear(clienteInvalido))
                .expectError(ValidacionException.class)
                .verify();

        verify(repository, never()).save(any());
    }

    @SuppressWarnings("null")
    @Test
    void crear_ClienteConEmailVacio_LanzaValidacionException() {
        Cliente clienteInvalido = new Cliente(null, "Nombre", "");

        StepVerifier.create(service.crear(clienteInvalido))
                .expectError(ValidacionException.class)
                .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void listar_CuandoHayClientes_RetornaFluxConClientes() {
        Cliente cliente2 = new Cliente(2L, "Ana Gómez", "ana@example.com");
        when(repository.findAll()).thenReturn(Flux.just(cliente, cliente2));

        StepVerifier.create(service.listar())
                .expectNext(cliente)
                .expectNext(cliente2)
                .verifyComplete();
    }

    @Test
    void listar_CuandoNoHayClientes_RetornaFluxVacio() {
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(service.listar())
                .expectNextCount(0)
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void obtenerPorId_CuandoExiste_RetornaCliente() {
        when(repository.findById(CLIENTE_ID)).thenReturn(Mono.just(cliente));

        StepVerifier.create(service.obtenerPorId(CLIENTE_ID))
                .expectNext(cliente)
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void obtenerPorId_CuandoNoExiste_LanzaRecursoNoEncontradoException() {
        when(repository.findById(CLIENTE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(service.obtenerPorId(CLIENTE_ID))
                .expectError(RecursoNoEncontradoException.class)
                .verify();
    }

    @SuppressWarnings("null")
    @Test
    void actualizar_CuandoExiste_RetornaClienteActualizado() {
        Cliente nuevo = new Cliente(null, "Nuevo Nombre", "nuevo@email.com");
        Cliente actualizado = new Cliente(CLIENTE_ID, "Nuevo Nombre", "nuevo@email.com");

        when(repository.findById(CLIENTE_ID)).thenReturn(Mono.just(cliente));
        when(repository.save(any(Cliente.class))).thenReturn(Mono.just(actualizado));

        StepVerifier.create(service.actualizar(CLIENTE_ID, nuevo))
                .expectNext(actualizado)
                .verifyComplete();

        verify(repository).save(argThat(cliente ->
            cliente.getId().equals(CLIENTE_ID) &&
            "Nuevo Nombre".equals(cliente.getNombre()) &&
            "nuevo@email.com".equals(cliente.getEmail())
        ));
    }

    @SuppressWarnings("null")
    @Test
    void actualizar_CuandoNoExiste_LanzaRecursoNoEncontradoException() {
        when(repository.findById(CLIENTE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(service.actualizar(CLIENTE_ID, cliente))
                .expectError(RecursoNoEncontradoException.class)
                .verify();

        verify(repository, never()).save(any());
    }

    @SuppressWarnings("null")
    @Test
    void eliminar_CuandoExiste_CompletaSinErrores() {
        when(repository.findById(CLIENTE_ID)).thenReturn(Mono.just(cliente));
        when(repository.delete(cliente)).thenReturn(Mono.empty());

        StepVerifier.create(service.eliminar(CLIENTE_ID))
                .verifyComplete();

        verify(repository).delete(cliente);
    }

    @SuppressWarnings("null")
    @Test
    void eliminar_CuandoNoExiste_LanzaRecursoNoEncontradoException() {
        when(repository.findById(CLIENTE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(service.eliminar(CLIENTE_ID))
                .expectError(RecursoNoEncontradoException.class)
                .verify();

        verify(repository, never()).delete(any());
    }
}