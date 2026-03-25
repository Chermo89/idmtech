package com.guillermo.idmtech.webapp.spring.controller;

import com.guillermo.idmtech.webapp.spring.model.Cliente;
import com.guillermo.idmtech.webapp.spring.service.ClienteService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.test.web.reactive.server.WebTestClient;

public class ClienteControllerTest {
    private final ClienteService service = Mockito.mock(ClienteService.class);

    private final WebTestClient client =
            WebTestClient.bindToController(new ClienteController(service)).build();

    //GET ALL
    @Test
    void listar() {
        Mockito.when(service.listar())
                .thenReturn(Flux.just(new Cliente(1L, "Juan", "test@test.com")));

        client.get()
                .uri("/clientes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Cliente.class)
                .hasSize(1);
    }

    //GET BY ID
    @Test
    void obtenerPorId() {
        Mockito.when(service.obtenerPorId(1L))
                .thenReturn(Mono.just(new Cliente(1L, "Juan", "test@test.com")));

        client.get()
                .uri("/clientes/1")
                .exchange()
                .expectStatus().isOk();
    }

    //POST
    @Test
    void crear() {
        Cliente cliente = new Cliente(1L, "Juan", "test@test.com");

        Mockito.when(service.crear(Mockito.any()))
                .thenReturn(Mono.just(cliente));

        client.post()
                .uri("/clientes")
                .bodyValue(cliente)
                .exchange()
                .expectStatus().isOk();
    }

    //DELETE
    @Test
    void eliminar() {
        Mockito.when(service.eliminar(1L))
                .thenReturn(Mono.empty());

        client.delete()
                .uri("/clientes/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
