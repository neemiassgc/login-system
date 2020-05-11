package com.spring.web.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spring.web.database.Transaction;
import com.spring.web.entity.Account;
import com.spring.web.mapper.AccountMapper;
import com.spring.web.services.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public final class SessionController {

    @Autowired
    private Transaction transaction;

    @Autowired
    private Token token;

    @PostMapping(path = "/session/signin")
    public ResponseEntity<String> login(
        @RequestParam(name = "email", defaultValue = "foo@example.com") String email,
        @RequestParam(name = "password", defaultValue = "1234") String password,
        @RequestParam(name = "remember", defaultValue = "false") boolean remember) {

        if(email.matches("[a-zA-Z0-9._]{3,}@\\w+\\.\\w+") && password.length() >= 8) {
            Account account = this.transaction.getAccount(email, password);

            if(account != null) {
                return ResponseEntity.status(HttpStatus.OK)
                    .header("Set-Cookie",
                    String.format("TOKEN=%s; Path=/; Max-Age="+(remember ? "604800" : "30")+"; Secure; HttpOnly;",
                    this.token.genToken(
                    account.getName(),
                    account.getSurname(),
                    account.getBirthday(),
                    account.getAge(),
                    120, account.getId(),
                    account.getEmail(),
                    account.getFavoriteColor()))).body("Login successfully");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping(path = "/session/logout")
    public ResponseEntity<String> logout(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Set-Cookie", "TOKEN=0; Path=/; Max-Age=0; Secure; HttpOnly;");
        return ResponseEntity.ok("Logout successfully");
    }

    @PostMapping(path = "/session/signup")
    public String signup(@RequestParam Map<String, String> params, Model model) {
        if(params.keySet().size() != 7) {
            return String.format("redirect:/fail?msg=%s", "it not possible register");
        }

        boolean checking =
        params.get("name").length() <= 50 && params.get("name").length() >= 3 &&
        params.get("surname").length() <= 50 && params.get("surname").length() >= 3 &&
        params.get("birthday").length() == 10 && new Integer(params.get("age")).intValue() >= 18 &&
        new Integer(params.get("age")).intValue() <= 100 && params.get("color").length() >= 3 &&
        !this.transaction.checkEmail(params.get("email")) && params.get("password").length() >= 8;

        if(!checking) {
            return String.format("redirect:/fail?msg=%s", "it not possible register");
        }

        this.transaction.registerAccount(AccountMapper.makeAccount(params));

        return String.format("redirect:/login?withEmail=%s", params.get("email"));
    }

    @GetMapping(path = "/validate/check-email")
    public ResponseEntity<String> checkEmail(
            @RequestParam(name = "email", defaultValue = "foo@bar.com") String email) {

        if(email.matches("[a-zA-Z0-9._]{3,}@\\w+\\.\\w+") && email.length() <= 120) {
            if(!this.transaction.checkEmail(email)) {
                return ResponseEntity.status(HttpStatus.OK).body("This email is available");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("This email is not available");
    }

    @PutMapping(path = "/session/update")
    public ResponseEntity<String> updateUser(
            @CookieValue(name = "TOKEN", defaultValue = "") String tokenCookie,
            @RequestBody Map<String, Object> reqBody, HttpServletResponse httpServletResponse) {
        if(tokenCookie.equals("")) {
            httpServletResponse.setHeader("Set-Cookie", "TOKEN=0; Path=/; Max-Age=0; Secure; HttpOnly;");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value()).body("Token doesn't exist");
        }

        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = this.token.checkToken(tokenCookie);
        }
        catch(SignatureVerificationException | InvalidClaimException sveice) {
            httpServletResponse.setHeader("Set-Cookie", "TOKEN=0; Path=/; Max-Age=0; Secure; HttpOnly;");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Update failed");
        }
        catch(TokenExpiredException ignored) {}
        finally {
            decodedJWT = JWT.decode(tokenCookie);

            Account account = new Account();
            account.setName((String) reqBody.get("name"));
            account.setSurname((String) reqBody.get("surname"));
            account.setEmail((String) reqBody.get("email"));
            account.setBirthday((String) reqBody.get("birthday"));
            account.setAge(new Integer((String) reqBody.get("age")).intValue());
            account.setFavoriteColor((String) reqBody.get("color"));

            httpServletResponse.setHeader("Set-Cookie",
                String.format("TOKEN=%s; Path=/; Max-Age=604800; Secure; HttpOnly;",
                this.token.genToken(
                account.getName(),
                account.getSurname(),
                account.getBirthday(),
                account.getAge(),
                300, decodedJWT.getClaim("sub").asInt(),
                account.getEmail(),
                account.getFavoriteColor())));

            this.transaction.updateAccount(decodedJWT.getClaim("sub").asInt(), account);

            if(((String)reqBody.get("password")).length() >= 8) {
                this.transaction.updateAccountPassword(decodedJWT.getClaim("sub").asInt(),
                    (String) reqBody.get("password"));
            }
        }

        return ResponseEntity.ok("Update successfully");
    }
}
