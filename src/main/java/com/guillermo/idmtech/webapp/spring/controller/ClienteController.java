package com.guillermo.idmtech.webapp.spring.controller;

import com.guillermo.idmtech.webapp.spring.model.Cliente;
import com.guillermo.idmtech.webapp.spring.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Cliente>> crear(@RequestBody Cliente cliente) {
        return service.crear(cliente)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Cliente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Cliente>> obtener(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Cliente>> actualizar(@PathVariable Long id,
                                                    @RequestBody Cliente cliente) {
        return service.actualizar(id, cliente)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable Long id) {
        return service.eliminar(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

}
