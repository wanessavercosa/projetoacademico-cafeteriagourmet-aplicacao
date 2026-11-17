package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.dto.CadastroEnderecoRequest;
import com.chouchougourmet.projeto.dto.EnderecoResponseDTO;
import com.chouchougourmet.projeto.model.entity.Cliente;
import com.chouchougourmet.projeto.model.entity.Endereco;
import com.chouchougourmet.projeto.repository.ClienteRepository;
import com.chouchougourmet.projeto.repository.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private EnderecoService enderecoService;

    @Test
    void deveCadastrarEnderecoEDefinirComoPadrao() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        CadastroEnderecoRequest dados = new CadastroEnderecoRequest(
                1L, "Rua Floriano Peixoto", "123", "Apto",
                "Centro", "Recife", "01000-000", true);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(inv -> {
            Endereco e = inv.getArgument(0);
            e.setId(4L);
            return e;
        });

        Endereco resultado = enderecoService.cadastrar(dados);

        assertEquals(4L, resultado.getId());
        assertEquals(4L, cliente.getEnderecoPadraoId());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void naoDeveCadastrarQuandoClienteNaoExiste() {
        CadastroEnderecoRequest dados = new CadastroEnderecoRequest(
                99L, "Rua X", "1", null, "Centro", "Recife", "00000-000", false);

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> enderecoService.cadastrar(dados));
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    void deveListarEnderecosPorCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEnderecoPadraoId(2L);

        Endereco endereco = new Endereco();
        endereco.setId(2L);
        endereco.setCliente(cliente);
        endereco.setRua("Rua Floriano Peixoto");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.findByClienteId(1L)).thenReturn(List.of(endereco));

        List<EnderecoResponseDTO> resultado = enderecoService.listarPorCliente(1L);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).padrao());
    }
}
