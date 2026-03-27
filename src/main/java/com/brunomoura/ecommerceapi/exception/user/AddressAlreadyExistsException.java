package com.brunomoura.ecommerceapi.exception.user;

public class AddressAlreadyExistsException extends RuntimeException {
  public AddressAlreadyExistsException(String message) {
    super(message);
  }
}
