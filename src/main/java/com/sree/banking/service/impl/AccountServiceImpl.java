package com.sree.banking.service.impl;

import com.sree.banking.dto.AccountDto;
import com.sree.banking.dto.TransactionDto;
import com.sree.banking.dto.TransferFundDto;
import com.sree.banking.entity.Account;
import com.sree.banking.entity.Transaction;
import com.sree.banking.exception.AccountException;
import com.sree.banking.mapper.AccountMapper;
import com.sree.banking.mapper.TransactionMapper;
import com.sree.banking.repository.AccountRepository;
import com.sree.banking.repository.TransactionRepository;
import com.sree.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
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
                        () -> new AccountException("Account does not exist")
                );
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(
                        () -> new AccountException("Account does not exist")
                );

        account.setBalance(account.getBalance() + amount);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimeStamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(
                        () -> new AccountException("Account does not exist")
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

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setTimeStamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAccounts() {
        List<Account> accountList = accountRepository.findAll();
        List<AccountDto> accountDtoList = new ArrayList<>();
        for (Account acc : accountList) {
            AccountDto accountDto = new AccountDto(acc.getId(),acc.getAccountHolderName(),acc.getBalance());
            accountDtoList.add(accountDto);
        }
        return accountDtoList;
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(
                        () -> new AccountException("Account does not exist")
                );

        accountRepository.deleteById(id);
    }

    @Override
    public void transferFunds(TransferFundDto transferFundDto) {

        Account fromAccount = accountRepository
                .findById(transferFundDto.fromAccountId())
                .orElseThrow(
                        () -> new AccountException("Account does not exist")
                );

        Account toAccount = accountRepository
                .findById(transferFundDto.toAccountId())
                .orElseThrow(
                        () -> new AccountException("Account does not exist")
                );

        if( transferFundDto.amount() <= 0 ){
            throw new RuntimeException("Enter a valid amount");
        }
        if (transferFundDto.amount() < fromAccount.getBalance()) {
            fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());
        } else {
            throw new RuntimeException("You dont have enough balance");
        }
        accountRepository.save(fromAccount);
        toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setAccountId(transferFundDto.fromAccountId());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        transaction.setTimeStamp(LocalDateTime.now());
        transactionRepository.save(transaction);

    }

    @Override
    public List<TransactionDto> getAccountTransactions(Long accountId) {

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimeStampDesc(accountId);

        return transactions.stream()
                .map((transaction -> TransactionMapper.mapToTransactionDto(transaction)))
                .collect(Collectors.toList());
    }
}
