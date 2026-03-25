package com.guillermo.idmtech.webapp.spring.service.impl;

import com.guillermo.idmtech.webapp.spring.exception.BusinessException;
import com.guillermo.idmtech.webapp.spring.model.Cliente;
import com.guillermo.idmtech.webapp.spring.repository.ClienteRepository;
import com.guillermo.idmtech.webapp.spring.service.ClienteService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

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
                .map(this::validar)
                .flatMap(repository::save)
                .onErrorResume(e -> Mono.error(new BusinessException("Error creando cliente")));
    }

    @Override
    public Flux<Cliente> listar() {
        return repository.findAll()
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Cliente> obtenerPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Cliente no encontrado")));
    }

    @Override
    public Mono<Cliente> actualizar(Long id, Cliente cliente) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Cliente no encontrado")))
                .flatMap(existente ->
                        Mono.just(cliente)
                                .map(c -> actualizarCampos(existente, c))
                                .flatMap(repository::save)
                )
                .onErrorResume(e -> Mono.error(new BusinessException("Error actualizando cliente")));
    }

    @Override
    public Mono<Void> eliminar(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Cliente no encontrado")))
                .flatMap(repository::delete);
    }

    // Programación funcional con Optional
    private Cliente validar(Cliente cliente) {
        return Optional.ofNullable(cliente)
                .filter(c -> c.getNombre() != null && !c.getNombre().isBlank())
                .filter(c -> c.getEmail() != null && !c.getEmail().isBlank())
                .orElseThrow(() -> new BusinessException("Datos inválidos"));
    }

    // Inmutabilidad + Optional
    private Cliente actualizarCampos(Cliente existente, Cliente nuevo) {
        String nombre = Optional.ofNullable(nuevo.getNombre()).orElse(existente.getNombre());
        String email = Optional.ofNullable(nuevo.getEmail()).orElse(existente.getEmail());

        return new Cliente(existente.getId(), nombre, email);
    }
}
