package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.UserPostRequest;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.dtos.user.UserUpdateRequest;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserAdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Listar todos os usuários",
            description = """
        Retorna a lista completa de usuários cadastrados no sistema.
        Esse endpoint é destinado para uso administrativo e requer autenticação com permissão adequada.
        """
    )
    @GetMapping
    public List<UserResponse> findAll() {
        return UserMapper.INSTANCE.toUserResponseList(adminService.findAll());
    }

    @Operation(
            summary = "Buscar usuário por ID",
            description = """
        Retorna os dados de um usuário específico pelo seu identificador UUID.
        Requer autenticação com permissão administrativa.
        """
    )
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return UserMapper.INSTANCE.toUserResponse(adminService.findById(id));
    }

    @Operation(
            summary = "Criar um novo usuário (Admin)",
            description = """
        Permite ao administrador criar um novo usuário com nome, e-mail, senha e papel (role) definidos.
        Uma carteira digital é automaticamente criada e associada ao usuário.
        A senha é armazenada de forma segura com criptografia.
        Retorna os dados do usuário criado com status HTTP 201.
        """
    )
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserPostRequest userPostRequest) {
        User user = adminService.createUser(UserMapper.INSTANCE.toUser(userPostRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Atualizar dados de um usuário (Admin)",
            description = """
        Permite ao administrador atualizar as informações de um usuário existente, incluindo nome, e-mail, senha e papel (role).
        Campos não informados permanecem inalterados.
        Retorna os dados atualizados do usuário.
        """
    )
    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        User user = adminService.updateUser(UserMapper.INSTANCE.toUser(userUpdateRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Excluir usuário pelo ID (Admin)",
            description = """
        Remove permanentemente o usuário identificado pelo UUID fornecido.
        As carteiras e transações associadas **não são deletadas**, pois são registros permanentes da blockchain.
        """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
