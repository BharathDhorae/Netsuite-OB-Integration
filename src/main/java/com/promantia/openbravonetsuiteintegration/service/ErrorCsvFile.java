package com.promantia.openbravonetsuiteintegration.service;

public record ErrorCsvFile(String source, String fileName, byte[] contents) {
}