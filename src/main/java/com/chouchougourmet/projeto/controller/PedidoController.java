package com.chouchougourmet.projeto.controller;

import com.chouchougourmet.projeto.dto.FinalizarPedidoRequest;
import com.chouchougourmet.projeto.dto.PedidoResponseDTO;
import com.chouchougourmet.projeto.model.entity.Pedido;
import com.chouchougourmet.projeto.model.enums.StatusPedido;
import com.chouchougourmet.projeto.service.PedidoService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedi*os")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/cliente/{clienteId}/finalizar")
    public ResponseEntity<PedidoResponseDTO> finalizarPedido(
          @PathVariable Long clienteId,
          @Valid @RequestBody FinalizarPedidoRequest dados) {
        Pedido pedido = pedidoService.criarPedido(
                clienteId,
                dados.getEnderecoId(),
                dados.getCodigoCupom(),
                dados.getDistanciaKm()
        );

       return ResponseEntity.status(HttpStatus.CREATED)
                .body(converterParaDTO(pedido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
           @PathVariable Long id) {

       Pedido pedido = pedidoService.buscarPorId(id);

        return ResponseEntity.ok(
                converterParaDTO(pedido)
        );
   }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId) {

        List<PedidoResponseDTO> pedidos =
                pedidoService.listarPorCliente(clienteId)
                        .stream()
                        .map(this::converterParaDTO)
                        .toList();

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/cliente/{clienteId}/status/{status}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorStatus(
            @PathVariable Long clienteId,
            @PathVariable StatusPedido status) {

        List<PedidoResponseDTO> pedidos =
                pedidoService.listarPorStatus(clienteId, status)
                        .stream()
                        .map(this::converterParaDTO)
                        .toList();

        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<PedidoResponseDTO> confirmarPedido(
            @PathVariable Long id) {

        Pedido pedido =
                pedidoService.confirmarPedido(id);

        return ResponseEntity.ok(
                converterParaDTO(pedido)
        );
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
            @PathVariable Long id) {

        Pedido pedido =
                pedidoService.cancelarPedido(id);

        return ResponseEntity.ok(
                converterParaDTO(pedido)
        );
    }

    private PedidoResponseDTO converterParaDTO(
            Pedido pedido) {

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getStatus().name(),
                pedido.getDataPedido(),
                pedido.getValorItens(),
                pedido.getValorDesconto(),
                pedido.getTaxaEntrega(),
                pedido.getValorTotal()
        );
    }
}