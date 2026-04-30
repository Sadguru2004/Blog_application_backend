package com.sadguru.blogapplication.services;

import com.sadguru.blogapplication.payloads.PageResponse;
import com.sadguru.blogapplication.payloads.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO,Integer userId);

    UserDTO getUserById(Integer userId);

    PageResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    void deleteUser(Integer userId);
}




