package com.sree.banking.mapper;

import com.sree.banking.dto.TransactionDto;
import com.sree.banking.entity.Transaction;

public class TransactionMapper {

    public static TransactionDto mapToTransactionDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimeStamp()
        );

        return transactionDto;
    }
}
