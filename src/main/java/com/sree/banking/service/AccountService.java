package com.sree.banking.service;

import com.sree.banking.dto.AccountDto;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long id);

    AccountDto deposit(Long id, double amount);
}
