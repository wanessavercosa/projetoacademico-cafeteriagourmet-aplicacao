package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.dto.CadastroClienteRequest;
import com.chouchougourmet.projeto.model.entity.Cliente;
import com.chouchougourmet.projeto.repository.ClienteRepository;
import com.chouchougourmet.projeto.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveCadastrarClienteComSucesso() {

        CadastroClienteRequest request =
                new CadastroClienteRequest(
                        "Maria",
                        "maria@email.com",
                        "123456",
                        "999999999"
                );

        when(usuarioRepository.existsByEmail(
                request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("senhaCriptografada");

        when(clienteRepository.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Cliente cliente =
                clienteService.cadastrar(request);

        verify(passwordEncoder)
                .encode("123456");

        verify(clienteRepository)
                .save(any(Cliente.class));
    }

    @Test
    void naoDevePermitirEmailDuplicado() {

        CadastroClienteRequest request =
                new CadastroClienteRequest(
                        "Maria",
                        "maria@email.com",
                        "123456",
                        "999999999"
                );

        when(usuarioRepository.existsByEmail(
                request.email()))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService.cadastrar(request)
                );

        assertEquals(
                "Este e-mail já está cadastrado.",
                exception.getMessage()
        );

        verify(clienteRepository, never())
                .save(any());

        verify(passwordEncoder, never())
                .encode(any());
    }
}