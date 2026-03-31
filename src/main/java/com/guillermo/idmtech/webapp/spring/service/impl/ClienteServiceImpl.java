package com.guillermo.idmtech.webapp.spring.service.impl;

import com.guillermo.idmtech.webapp.spring.exception.RecursoNoEncontradoException;
import com.guillermo.idmtech.webapp.spring.exception.ValidacionException;
import com.guillermo.idmtech.webapp.spring.model.Cliente;
import com.guillermo.idmtech.webapp.spring.repository.ClienteRepository;
import com.guillermo.idmtech.webapp.spring.service.ClienteService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository repository;

    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Cliente> crear(Cliente cliente) {
        return Mono.just(cliente)
            .map(this::validar) // lanza ValidacionException si falla
            .flatMap(repository::save);
            // si save falla (DataAccessException), se propaga tal cual
    }

    @Override
    public Flux<Cliente> listar() {
        return repository.findAll()
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Cliente> obtenerPorId(Long id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return repository.findById(id)
            .switchIfEmpty(Mono.error(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id)));
    }

    @Override
    public Mono<Cliente> actualizar(Long id, Cliente cliente) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo");

        return repository.findById(id)
                .switchIfEmpty(Mono.error(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id)))
                .flatMap(existente -> {
                    Cliente actualizado = actualizarCampos(existente, cliente);
                    return repository.save(actualizado); // actualizado nunca es nulo
                });
    }

    @Override
    public Mono<Void> eliminar(Long id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return repository.findById(id)
            .switchIfEmpty(Mono.error(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id)))
            .flatMap(repository::delete);
    }

    // Programación funcional con Optional
    private Cliente validar(Cliente cliente) {
        return Optional.ofNullable(cliente)
                .filter(c -> c.getNombre() != null && !c.getNombre().isBlank())
                .filter(c -> c.getEmail() != null && !c.getEmail().isBlank())
                .orElseThrow(() -> new ValidacionException("Datos inválidos"));
    }

    // Inmutabilidad + Optional
    @NonNull
    private Cliente actualizarCampos(Cliente existente, Cliente nuevo) {
        String nombre = Optional.ofNullable(nuevo.getNombre()).orElse(existente.getNombre());
        String email = Optional.ofNullable(nuevo.getEmail()).orElse(existente.getEmail());

        return new Cliente(existente.getId(), nombre, email);
    }
}
