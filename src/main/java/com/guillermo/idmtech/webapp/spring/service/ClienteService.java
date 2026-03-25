package com.guillermo.idmtech.webapp.spring.service;

import com.guillermo.idmtech.webapp.spring.model.Cliente;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteService {
    Mono<Cliente> crear(Cliente cliente);
    Flux<Cliente> listar();
    Mono<Cliente> obtenerPorId(Long id);
    Mono<Cliente> actualizar(Long id, Cliente cliente);
    Mono<Void> eliminar(Long id);
}