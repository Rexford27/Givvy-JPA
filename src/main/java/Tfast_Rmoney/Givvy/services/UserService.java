package Tfast_Rmoney.Givvy.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            return "user exists, duplicate";
        }

        String hashedPassword = passwordService.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        user.setSatisfactionScore(0.0);

        User savedUser = userRepository.save(user);

        return savedUser.getUserId().toString();
    }

    public boolean loginUser(String email, String password) {

        Optional<User> possibleUser = userRepository.findByEmail(email);

        if (possibleUser.isEmpty()) {
            return false;
        }

        User user = possibleUser.get();

        return passwordService.verifyHash(password, user.getPasswordHash());
    }

    public Optional<User> loginAndReturnUser(String email, String password) {

        Optional<User> possibleUser = userRepository.findByEmail(email);

        if (possibleUser.isEmpty()) {
            return Optional.empty();
        }

        User user = possibleUser.get();

        boolean passwordMatches = passwordService.verifyHash(
                password,
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            return Optional.empty();
        }

        return possibleUser;
    }

    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}