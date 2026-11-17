package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.dto.CadastroClienteRequest;
import com.chouchougourmet.projeto.model.entity.Cliente;
import com.chouchougourmet.projeto.repository.ClienteRepository;
import com.chouchougourmet.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Cliente cadastrar(CadastroClienteRequest dados) {

        // 1. Verificar se o e-mail já existe
        if (usuarioRepository.existsByEmail(dados.email())) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado."
            );
        }

        // 2. Criar o cliente
        Cliente cliente = new Cliente();

        cliente.setNome(dados.nome());
        cliente.setEmail(dados.email());
        cliente.setCelular(dados.celular());

        // 3. Criptografar a senha
        String senhaCriptografada =
                passwordEncoder.encode(dados.senha());

        cliente.setSenhaHash(senhaCriptografada);

        // 4. Salvar no banco de dados
        return clienteRepository.save(cliente);
    }

    public Cliente login(
            String email,
            String senha) {

        Cliente cliente =
                clienteRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Usuário não encontrado."
                                ));

        if (!passwordEncoder.matches(
                senha,
                cliente.getSenhaHash())) {

            throw new IllegalArgumentException(
                    "Senha inválida."
            );
        }

        return cliente;
    }
}
