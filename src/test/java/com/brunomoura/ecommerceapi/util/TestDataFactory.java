package com.brunomoura.ecommerceapi.util;

import com.brunomoura.ecommerceapi.dto.auth.LoginRequestDTO;
import com.brunomoura.ecommerceapi.dto.user.AddressUpdateDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class TestDataFactory {

    public static UserCreateRequestDTO createValidUserRequest() {
        Set<AddressUpdateDTO> addresses = Set.of(new AddressUpdateDTO(
                "Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Curiuva",
                "Paraná",
                "Brasil",
                "81800000"
        ));

        return new UserCreateRequestDTO(
                "Jorge Antonio Erick",
                generateUniqueEmail(),
                generateUniqueCpf(),
                "41995925262",
                LocalDate.of(2000, 10,22),
                "Password@123",
                addresses
        );
    }

    private static String generateUniqueEmail() {

        return "test" + UUID.randomUUID().toString().replace("-","") + "@email.com";
    }

    private static String generateUniqueCpf() {
        Random random = new Random();

        int[] cpf = new int[11];

        for (int i = 0; i < 9; i++) {
            cpf[i] = random.nextInt(10);
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += cpf[i] * (10 - i);
        }

        int resto = soma % 11;
        cpf[9] = (resto < 2) ? 0 : 11 - resto;

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += cpf[i] * (11 - i);
        }

        resto = soma % 11;
        cpf[10] = (resto < 2) ? 0 : 11 - resto;

        if (allDigitsEqual(cpf)) {
            return generateUniqueCpf();
        }

        StringBuilder stringCpf = new StringBuilder();
        for (int num : cpf) {
            stringCpf.append(num);
        }

        return stringCpf.toString();
    }

    private static boolean allDigitsEqual(int[] cpf) {
        int first = cpf[0];

        for (int i = 1; i < cpf.length; i++) {
            if (cpf[i] != first) {
                return false;
            }
        }
        return true;
    }
}
