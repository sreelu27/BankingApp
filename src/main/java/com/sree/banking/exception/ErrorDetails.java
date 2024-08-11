package com.sree.banking.exception;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime localDateTime,
                           String message,
                           String details,
                           String errorCode) {
}
