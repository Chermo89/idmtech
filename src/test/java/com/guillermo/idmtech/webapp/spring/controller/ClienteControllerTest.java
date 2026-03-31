package com.guillermo.idmtech.webapp.spring.controller;

import com.guillermo.idmtech.webapp.spring.exception.RecursoNoEncontradoException;
import com.guillermo.idmtech.webapp.spring.exception.ValidacionException;
import com.guillermo.idmtech.webapp.spring.model.Cliente;
import com.guillermo.idmtech.webapp.spring.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = ClienteController.class)
class ClienteControllerTest {
        @Autowired
        private WebTestClient webTestClient;

        @MockitoBean
        private ClienteService service;

        private Cliente cliente;
        private final Long CLIENTE_ID = 1L;

        @BeforeEach
        void setUp() {
                cliente = new Cliente(CLIENTE_ID, "Juan Pérez", "juan@example.com");
        }

        @SuppressWarnings("null")
        @Test
        void crear_ClienteValido_Retorna200() {
                when(service.crear(any(Cliente.class))).thenReturn(Mono.just(cliente));
                webTestClient.post()
                        .uri("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(cliente)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(Cliente.class)
                        .isEqualTo(cliente);
        }

        @SuppressWarnings("null")
        @Test
        void crear_ClienteInvalido_Retorna400() {
                when(service.crear(any(Cliente.class)))
                .thenReturn(Mono.error(new ValidacionException("Datos inválidos")));

        webTestClient.post()
                .uri("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Cliente(null, "", ""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("VALIDATION_FAILED");
        }

        @Test
        void listar_RetornaListaDeClientes() {
                Cliente cliente2 = new Cliente(2L, "Ana Gómez", "ana@example.com");
                when(service.listar()).thenReturn(Flux.just(cliente, cliente2));

                webTestClient.get()
                        .uri("/clientes")
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(Cliente.class)
                        .hasSize(2)
                        .contains(cliente, cliente2);
        }

        @Test
        void listar_CuandoNoHayClientes_RetornaListaVacia() {
                when(service.listar()).thenReturn(Flux.empty());

                webTestClient.get()
                        .uri("/clientes")
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(Cliente.class)
                        .hasSize(0);
        }

        @SuppressWarnings("null")
        @Test
        void obtenerPorId_CuandoExiste_RetornaCliente() {
                when(service.obtenerPorId(CLIENTE_ID)).thenReturn(Mono.just(cliente));

                webTestClient.get()
                        .uri("/clientes/{id}", CLIENTE_ID)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.id").isEqualTo(CLIENTE_ID)
                        .jsonPath("$.nombre").isEqualTo(cliente.getNombre())
                        .jsonPath("$.email").isEqualTo(cliente.getEmail());
        }

        @Test
        void obtenerPorId_CuandoNoExiste_Retorna404() {
                when(service.obtenerPorId(CLIENTE_ID))
                        .thenReturn(Mono.error(new RecursoNoEncontradoException("Cliente no encontrado")));

                webTestClient.get()
                        .uri("/clientes/{id}", CLIENTE_ID)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.code").isEqualTo("NOT_FOUND");
        }

        @SuppressWarnings("null")
        @Test
        void actualizar_CuandoExiste_RetornaClienteActualizado() {
                Cliente actualizado = new Cliente(CLIENTE_ID, "Nuevo Nombre", "nuevo@email.com");
                when(service.actualizar(eq(CLIENTE_ID), any(Cliente.class))).thenReturn(Mono.just(actualizado));

                webTestClient.put()
                        .uri("/clientes/{id}", CLIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(actualizado)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(Cliente.class)
                        .isEqualTo(actualizado);
        }

        @SuppressWarnings("null")
        @Test
        void actualizar_CuandoNoExiste_Retorna404() {
                when(service.actualizar(eq(CLIENTE_ID), any(Cliente.class)))
                        .thenReturn(Mono.error(new RecursoNoEncontradoException("Cliente no encontrado")));

                webTestClient.put()
                        .uri("/clientes/{id}", CLIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(cliente)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.code").isEqualTo("NOT_FOUND");
        }

        @Test
        void eliminar_CuandoExiste_Retorna204() {
                when(service.eliminar(CLIENTE_ID)).thenReturn(Mono.empty());

                webTestClient.delete()
                        .uri("/clientes/{id}", CLIENTE_ID)
                        .exchange()
                        .expectStatus().isNoContent();
        }

        @Test
        void eliminar_CuandoNoExiste_Retorna404() {
                when(service.eliminar(CLIENTE_ID))
                        .thenReturn(Mono.error(new RecursoNoEncontradoException("Cliente no encontrado")));

                webTestClient.delete()
                        .uri("/clientes/{id}", CLIENTE_ID)
                        .exchange()
                        .expectStatus().isNotFound();
        }

        @Test
        void errorGeneral_Retorna500() {
                when(service.listar()).thenReturn(Flux.error(new RuntimeException("Error interno")));

                webTestClient.get()
                        .uri("/clientes")
                        .exchange()
                        .expectStatus().is5xxServerError()
                        .expectBody()
                        .jsonPath("$.code").isEqualTo("INTERNAL_ERROR");
        }
}