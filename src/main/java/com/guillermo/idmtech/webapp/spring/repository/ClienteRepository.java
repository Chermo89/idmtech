package com.guillermo.idmtech.webapp.spring.repository;

import com.guillermo.idmtech.webapp.spring.model.Cliente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {
}