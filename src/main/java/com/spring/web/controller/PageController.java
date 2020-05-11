package com.spring.web.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spring.web.services.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public final class PageController {

    @Autowired
    private Token token;

    @GetMapping(path = "/")
    public String rootPage(@CookieValue(name = "TOKEN", defaultValue = "") String tokenCookie) {
        if(tokenCookie.length() > 0) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/login")
    public String loginPage(
            @RequestParam(name = "withEmail", defaultValue = "") String userEmail,
            Model model, @CookieValue(name = "TOKEN", defaultValue = "") String tokenCookie,
            HttpServletResponse httpServletResponse) {
        if(tokenCookie.length() > 0) {
            return "redirect:/home";
        }

        model.addAttribute("userEmail", userEmail);
        return "loginpage";
    }

    @GetMapping(path = "/register")
    public String registerPage(@CookieValue(name = "TOKEN", defaultValue = "") String tokenCookie,
            HttpServletResponse httpServletResponse) {
        if(tokenCookie.length() > 0) {
            return "redirect:/home";
        }
        return "registerpage";
    }

    @GetMapping(path = "/home")
    public String homePage(
            @CookieValue(name = "TOKEN", defaultValue = "") String tokenCookie,
            Model model, HttpServletResponse httpServletResponse) {
        if(tokenCookie.equals("")) {
            return "redirect:/login";
        }

        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = this.token.checkToken(tokenCookie);
        }
        catch(TokenExpiredException tee) {
            decodedJWT = JWT.decode(tokenCookie);
            System.out.println("token expired");

            httpServletResponse.setHeader("Set-Cookie",
                    String.format("TOKEN=%s; Path=/; Max-Age=604800; Secure; HttpOnly;",
                    this.token.genToken(
                        decodedJWT.getClaim("name").asString(),
                        decodedJWT.getClaim("surname").asString(),
                        decodedJWT.getClaim("birth").asString(),
                        decodedJWT.getClaim("age").asInt(),
                        300, decodedJWT.getClaim("sub").asInt(),
                        decodedJWT.getClaim("email").asString(),
                        decodedJWT.getClaim("color").asString())));
        }
        catch(SignatureVerificationException | InvalidClaimException sveice) {
            System.out.println("token's signature invalid");
            httpServletResponse.setHeader("Set-Cookie", "TOKEN=0; Path=/; Max-Age=0; Secure; HttpOnly");
            return "redirect:/login";
        }

        model.addAttribute("name", decodedJWT.getClaim("name").asString());
        model.addAttribute("surname", decodedJWT.getClaim("surname").asString());
        model.addAttribute("birthday", decodedJWT.getClaim("birth").asString());
        model.addAttribute("age", decodedJWT.getClaim("age").asInt());
        model.addAttribute("email", decodedJWT.getClaim("email").asString());
        model.addAttribute("color", decodedJWT.getClaim("color").asString());

        httpServletResponse.setStatus(HttpStatus.OK.value());
        return "homepage";
    }

    @GetMapping(path = "/fail")
    @ResponseBody
    public String failPage(
            @RequestParam(name = "msg", defaultValue = "") String msg,
            HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
        return msg;
    }
}
