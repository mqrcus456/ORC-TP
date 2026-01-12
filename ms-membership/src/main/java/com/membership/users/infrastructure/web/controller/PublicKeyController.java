package com.membership.users.infrastructure.web.controller;
import com.membership.users.infrastructure.security.RSAKeyUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/public-key")
public class PublicKeyController {

    private final RSAKeyUtil rsaKeyUtil;

    public PublicKeyController(RSAKeyUtil rsaKeyUtil) {
        this.rsaKeyUtil = rsaKeyUtil;
    }

    @GetMapping
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(rsaKeyUtil.loadPublicKey().getEncoded());
    }
}
