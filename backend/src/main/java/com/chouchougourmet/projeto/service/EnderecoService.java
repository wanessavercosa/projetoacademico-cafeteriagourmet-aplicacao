package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.dto.CadastroEnderecoRequest;
import com.chouchougourmet.projeto.dto.EnderecoResponseDTO;
import com.chouchougourmet.projeto.model.entity.Cliente;
import com.chouchougourmet.projeto.model.entity.Endereco;
import com.chouchougourmet.projeto.repository.ClienteRepository;
import com.chouchougourmet.projeto.repository.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public Endereco cadastrar(CadastroEnderecoRequest dados) {

        // 1. Buscar o cliente
        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cliente não encontrado."
                        )
                );

        // 2. Criar o endereço
        Endereco endereco = new Endereco();

        endereco.setCliente(cliente);
        endereco.setRua(dados.rua());
        endereco.setNumero(dados.numero());
        endereco.setComplemento(dados.complemento());
        endereco.setBairro(dados.bairro());
        endereco.setCidade(dados.cidade());
        endereco.setCep(dados.cep());

        // 3. Salvar o endereço
        Endereco enderecoSalvo = enderecoRepository.save(endereco);

        // 4. Definir como endereço padrão, se solicitado
        if (dados.definirComoPadrao()) {

            cliente.setEnderecoPadraoId(enderecoSalvo.getId());

            clienteRepository.save(cliente);
        }

        return enderecoSalvo;
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listarPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        return enderecoRepository.findByClienteId(clienteId).stream()
                .map(e -> toResponse(e, cliente))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listarTodos() {
        return enderecoRepository.findAll().stream()
                .map(e -> toResponse(e, e.getCliente()))
                .toList();
    }

    private EnderecoResponseDTO toResponse(Endereco e, Cliente cliente) {
        boolean padrao = e.getId().equals(cliente.getEnderecoPadraoId());
        return new EnderecoResponseDTO(
                e.getId(),
                cliente.getId(),
                e.getRua(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getCep(),
                padrao
        );
    }
}