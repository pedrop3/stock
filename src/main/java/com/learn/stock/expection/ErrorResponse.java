package com.learn.stock.expection;

import java.util.List;

public record ErrorResponse(int status, String message, String exceptionName , List<String> errors) {}