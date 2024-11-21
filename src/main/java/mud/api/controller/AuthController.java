package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import mud.api.common.Constants;
import mud.api.entity.Account;
import mud.api.manager.AccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AccountManager accountManager;

    // LOGIN ACCOUNT
    @PostMapping("/v1/auth")
    public JsonNode loginAccount(@RequestParam("u") String userid, @RequestParam("p") String password) {
        Account account = accountManager.getAccount(userid, password);

        if (account == null) {
            return null;
        }
        logger.info(String.format("loginAccount:%s%s", userid, password));

        return Constants.MAPPER.valueToTree(account);
    }

    // CREATE ACCOUNT
    @PutMapping("/v1/auth")
    public ResponseEntity createAccount(@RequestParam("u") String userid, @RequestParam("p") String password) {
        Account check = accountManager.createAccount(userid, password);

        if (check == null) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
