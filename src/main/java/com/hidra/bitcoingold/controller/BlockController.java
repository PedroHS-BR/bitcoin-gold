package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BlockController {

    private final BlockRepository blockRepository;
    private final BlockService blockService;

    @Operation(
            summary = "Minerar um novo bloco na blockchain Bitcoin Gold",
            description = """
        Cria um novo bloco que inclui as transações pendentes e executa a prova de trabalho,
        garantindo que o hash do bloco atenda à dificuldade ("0000" no início).
        O minerador recebe uma transação de recompensa.
        Atualiza os saldos das carteiras envolvidas e retorna o bloco minerado (HTTP 201).
        Requer autenticação via token JWT.
        """
    )
    @PostMapping
    public ResponseEntity<Block> MineBlock() {
        Block block = blockService.createBlock();
        return new ResponseEntity<>(block, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Buscar bloco por ID",
            description = """
        Retorna os dados de um bloco específico com base no seu identificador único.
        Caso o bloco não seja encontrado, retorna erro 400 com a mensagem 'Block not found'.
        """
    )
    @GetMapping("/{id}")
    public ResponseEntity<Block> getBlock(@PathVariable long id) {
        return new ResponseEntity<>(blockService.getBlock(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Listar todos os blocos da blockchain",
            description = """
        Retorna uma lista com todos os blocos minerados na blockchain Bitcoin Gold.
        Os blocos são retornados na ordem em que foram salvos no banco de dados.
        """
    )
    @GetMapping("/all")
    public ResponseEntity<List<Block>> getAllBlocks() {
        return new ResponseEntity<>(blockRepository.findAll(), HttpStatus.OK);
    }

    @Operation(
            summary = "Validar a integridade de um bloco",
            description = """
        Verifica se o hash atual do bloco corresponde ao hash gerado com base nos seus dados.
        Caso o hash seja compatível, o bloco é considerado válido. Caso contrário, é inválido.
        Retorna uma mensagem indicando o resultado da validação.
        """
    )
    @GetMapping("/validate/{id}")
    public ResponseEntity<String> validateBlock(@PathVariable long id) {
        return new ResponseEntity<>(blockService.ValidateBlock(id), HttpStatus.OK);
    }

}
