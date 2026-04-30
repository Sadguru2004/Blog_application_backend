package com.sadguru.blogapplication.services.impl;

import com.sadguru.blogapplication.entities.Role;
import com.sadguru.blogapplication.entities.User;
import com.sadguru.blogapplication.exceptions.ResourceNotFoundException;
import com.sadguru.blogapplication.payloads.PageResponse;
import com.sadguru.blogapplication.payloads.UserDTO;
import com.sadguru.blogapplication.repositories.RoleRepo;
import com.sadguru.blogapplication.repositories.UserRepo;
import com.sadguru.blogapplication.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;

    // DTO → Entity
    private User dtoToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    // Entity → DTO
    private UserDTO userToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = dtoToUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        User savedUser = userRepo.save(user);
        return userToDto(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setAbout(userDTO.getAbout());

        User updatedUser = userRepo.save(user);
        return userToDto(updatedUser);
    }

    @Override
    public UserDTO getUserById(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        return userToDto(user);
    }

    @Override
    public PageResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> pageUser = this.userRepo.findAll(pageable);

        List<UserDTO> userDTOs = pageUser.getContent()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());

        PageResponse<UserDTO> response = new PageResponse<>();

        response.setContent(userDTOs);
        response.setPageNumber(pageUser.getNumber());
        response.setPageSize(pageUser.getSize());
        response.setTotalElements(pageUser.getTotalElements());
        response.setTotalPages(pageUser.getTotalPages());
        response.setLastPage(pageUser.isLast());

        return response;
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        userRepo.delete(user);
    }
}