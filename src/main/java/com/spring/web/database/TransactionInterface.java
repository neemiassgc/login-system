package com.spring.web.database;

import com.spring.web.entity.Account;

interface TransactionInterface {

    boolean checkEmail(String email);

    void registerAccount(Account account);

    void updateAccount(int id, Account account);

    void updateAccountPassword(int id, String password);

    Account getAccount(String email, String password);
}
