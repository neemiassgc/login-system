package com.spring.web.mapper;

import com.spring.web.entity.Account;

import java.util.Map;

public final class AccountMapper {

    public static Account makeAccount(Map<String, String> mapper) {
        Account account = new Account();

        account.setName(mapper.get("name"));
        account.setSurname(mapper.get("surname"));
        account.setBirthday(mapper.get("birthday"));
        account.setAge(new Integer(mapper.get("age")));
        account.setFavoriteColor(mapper.get("color"));
        account.setEmail(mapper.get("email"));
        account.setPassword(mapper.get("password"));

        return account;
    }
}
