package com.brunomoura.ecommerceapi.service;

import com.brunomoura.ecommerceapi.dto.user.UserCreateResponseDTO;
import com.brunomoura.ecommerceapi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //region DEPENDENCIES
    private final UserRepository userRepository;
    //endregion

    //region CONSTRUCTOR
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //endregion

    //region METHODS
    public UserCreateResponseDTO create() {

    }
    //endregion


}
