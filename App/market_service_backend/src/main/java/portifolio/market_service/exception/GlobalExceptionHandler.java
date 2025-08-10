package portifolio.market_service.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.access.AccessDeniedException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.time.Instant;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllExceptions(Exception ex) {
        // Log da exceção (pode substituir por logger)
        ex.printStackTrace();

        ProblemDetail problem;

        if (ex instanceof BadCredentialsException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), "Usuário ou senha incorretos");
        } else if (ex instanceof AccountStatusException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Conta bloqueada ou desativada");
        } else if (ex instanceof AccessDeniedException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Acesso negado a este recurso");
        } else if (ex instanceof SignatureException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Assinatura JWT inválida");
        } else if (ex instanceof ExpiredJwtException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Token JWT expirado");
        } else {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro interno desconhecido");
        }

        // Adiciona timestamp e classe da exceção para facilitar debugging
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("exception", ex.getClass().getSimpleName());

        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Dados inválidos: " + ex.getMessage());
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("exception", ex.getClass().getSimpleName());
        return problem;
    }
}
