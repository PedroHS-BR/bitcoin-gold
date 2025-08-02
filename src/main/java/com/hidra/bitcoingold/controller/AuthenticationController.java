package com.hidra.bitcoingold.controller;


import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.TokenResponse;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.security.TokenService;
import com.hidra.bitcoingold.service.AuthorizationService;
import com.hidra.bitcoingold.service.TransactionService;
import com.hidra.bitcoingold.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;
    private final UserService userService;
    private final TransactionService transactionService;

    @Operation(
            summary = "Realiza autenticação do usuário e gera token JWT",
            description = """
        Recebe o e-mail e senha do usuário para autenticação.
        Em caso de sucesso, retorna um token JWT que deve ser usado para autenticação nas chamadas protegidas da API.
        
        Se as credenciais forem inválidas, retorna erro 400 com mensagem "Invalid email or password".
        """
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password());
            Authentication auth = this.authenticationManager.authenticate(usernamePassword);
            String token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (Exception e) {
            throw new BadRequestException("Invalid email or password");
        }
    }

    @Operation(
            summary = "Registra um novo usuário na plataforma",
            description = """
        Cria um novo usuário com nome, e-mail e senha. Uma carteira digital é automaticamente associada ao usuário. A senha é armazenada de forma segura com criptografia.
        
        Caso o número total de usuários cadastrados seja igual ou inferior a 100, o novo usuário recebe um bônus de boas-vindas via transação automática.
        
        Retorna os dados do usuário recém-criado com status HTTP 201 (Created).
        """
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerNewUser(@RequestBody @Valid RegisterUserPostRequest registerUserPostRequest) {
        User user = authorizationService.createRegularuser(UserMapper.INSTANCE.toUser(registerUserPostRequest));
        if (userService.howManyUsers() <= 100) {
            transactionService.newUserBonusTransaction(user);
        }
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Estabelece conexão com a rede Bitcoin Gold",
            description = """
        Endpoint utilizado para verificar a conectividade básica com a rede Bitcoin Gold.
        Pode ser usado como teste de disponibilidade ou health check da API.
        Retorna uma mensagem simples de confirmação da conexão.
        """
    )
    @GetMapping("/connect")
    public ResponseEntity<String> connect() {
        return ResponseEntity.ok("Connecting to Bitcoin");
    }

    @Operation(summary = "Retorna informações do desenvolvedor da API"
            , description = """
        GitHub: <a href='https://github.com/PedroHS-BR' target='_blank'>GITHUB</a><br>
        Linkedin: <a href='https://www.linkedin.com/in/pedro-henrique-543580205/' target='_blank'>LinkedIn</a><br>
        Email: pedro.hsilva.pe@gmail.com
        """
    )
    @GetMapping("/dev")
    public ResponseEntity<String> developer() {
        return ResponseEntity.ok("""
                GitHub: https://github.com/PedroHS-BR\s
                Linkedin https://www.linkedin.com/in/pedro-henrique-543580205/
                Email: pedro.hsilva.pe@gmail.com
                """);
    }
}
