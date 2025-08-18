package com.deliverytech.controller;

import com.deliverytech.dto.request.ClienteRequest;
import com.deliverytech.dto.response.ClienteResponse;
import com.deliverytech.model.Cliente;
import com.deliverytech.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody ClienteRequest request) {
        logger.info("Cadastro de cliente iniciado: {}", request.getEmail());

        Cliente cliente = Cliente.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .ativo(true)
                .build();

        Cliente salvo = clienteService.cadastrar(cliente);
        logger.debug("Cliente salvo com ID {}", salvo.getId());

        // Retorna 201 Created com a localização do novo recurso no header
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ClienteResponse(salvo.getId(), salvo.getNome(), salvo.getEmail(), salvo.getAtivo()));
    }

    @GetMapping
    public Page<ClienteResponse> listar(Pageable pageable) {
        logger.info("Listando todos os clientes ativos de forma paginada");
        Page<Cliente> clientesPaginados = clienteService.listarAtivos(pageable);
        return clientesPaginados.map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscar(@PathVariable Long id) {
        logger.info("Buscando cliente com ID: {}", id);
        return clienteService.buscarPorId(id)
                .map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        logger.info("Atualizando cliente ID: {}", id);

        Cliente atualizado = Cliente.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .build();

        Cliente salvo = clienteService.atualizar(id, atualizado);
        return ResponseEntity.ok(new ClienteResponse(salvo.getId(), salvo.getNome(), salvo.getEmail(), salvo.getAtivo()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> ativarDesativar(@PathVariable Long id) {
        logger.info("Alterando status do cliente ID: {}", id);
        clienteService.ativarDesativar(id);
        return ResponseEntity.noContent().build();
    }
}
