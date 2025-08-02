package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.RegularUserUpdateRequest;
import com.hidra.bitcoingold.dtos.user.UserDataResponse;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.service.UserService;
import com.hidra.bitcoingold.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final WalletService walletService;

    @Operation(
            summary = "Obter dados do usuário autenticado",
            description = """
        Retorna as informações do usuário atualmente autenticado no sistema, incluindo nome, e-mail, ID da carteira e saldo atual.
        """
    )
    @GetMapping()
    public ResponseEntity<UserDataResponse> getRegularUser() {
        User regularUser = userService.getRegularUser();
        UserDataResponse user = UserMapper.INSTANCE.toUserDataResponse(regularUser);
        user.setBalance(walletService.getWallet(regularUser.getWalletId()).getBalance());
        return new ResponseEntity<>(user , HttpStatus.OK);
    }

    @Operation(
            summary = "Atualizar dados do usuário autenticado",
            description = """
        Atualiza o nome, e-mail e/ou senha do usuário atualmente autenticado.
        Campos não informados permanecerão inalterados.
        """
    )
    @PutMapping
    public ResponseEntity<UserResponse> updateRegularUser(@RequestBody RegularUserUpdateRequest regularUserUpdateRequest) {
        User user = userService.updateUser(UserMapper.INSTANCE.toUser(regularUserUpdateRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Excluir usuário autenticado",
            description = """
        Remove permanentemente o usuário atualmente autenticado do sistema.
        As carteiras e transações associadas **não são deletadas**, pois são registros permanentes da blockchain.
        """
    )
    @DeleteMapping
    public ResponseEntity<UserResponse> deleteRegularUser() {
        User user = userService.deleteUser();
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.OK);
    }

}
