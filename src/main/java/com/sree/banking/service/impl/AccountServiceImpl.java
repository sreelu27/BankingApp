package com.sree.banking.service.impl;

import com.sree.banking.dto.AccountDto;
import com.sree.banking.entity.Account;
import com.sree.banking.mapper.AccountMapper;
import com.sree.banking.repository.AccountRepository;
import com.sree.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Account does not exist")
                );
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Account does not exist")
                );

        account.setBalance(account.getBalance() + amount);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Account does not exist")
                );
        if( amount <= 0 ){
            throw new RuntimeException("Enter a valid amount");
        }
        if (amount < account.getBalance()) {
            account.setBalance(account.getBalance() - amount);
        } else {
            throw new RuntimeException("You dont have enough balance");
        }
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAccounts() {
        List<Account> accountList = accountRepository.findAll();
        List<AccountDto> accountDtoList = new ArrayList<>();
        for (Account acc : accountList) {
            AccountDto accountDto = new AccountDto();
            accountDto.setId(acc.getId());
            accountDto.setAccountHolderName(acc.getAccountHolderName());
            accountDto.setBalance(acc.getBalance());
            accountDtoList.add(accountDto);
        }
        return accountDtoList;
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Account does not exist")
                );

        accountRepository.deleteById(id);
    }
}
