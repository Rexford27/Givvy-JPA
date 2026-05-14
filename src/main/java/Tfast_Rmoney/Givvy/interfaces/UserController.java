package Tfast_Rmoney.Givvy.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.interfaces.dtos.ItemResponse;
import Tfast_Rmoney.Givvy.interfaces.dtos.LoginRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.RegisterUserRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.UserResponse;
import Tfast_Rmoney.Givvy.services.ItemService;
import Tfast_Rmoney.Givvy.services.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final ItemService itemService;

    public UserController(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    // POST /users/login
    @PostMapping("/login")
    public ResponseEntity<String> checkLogin(@RequestBody LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Empty email or password");
        }

        Optional<User> result = userService.loginAndReturnUser(
                request.getEmail(),
                request.getPassword()
        );

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        return ResponseEntity.ok(result.get().getUserId().toString());
    }

    // POST /users
    @PostMapping
    public ResponseEntity<String> save(@RequestBody RegisterUserRequest request) {

        if (request.getName() == null || request.getName().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Empty name, email, or password");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setPhone(request.getPhone());

        String key = userService.registerUser(user);

        if (key.equals("user exists, duplicate")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User with this email already exists");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(key);
    }

    // GET /users/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<User> possibleUser = userService.findUserById(possibleUserId.get());

        if (possibleUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        UserResponse response = new UserResponse(possibleUser.get());

        return ResponseEntity.ok(response);
    }

    // GET /users/{userId}/items
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<ItemResponse>> getItemsByUser(@PathVariable String userId) {

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        List<Item> items = itemService.findItemsByUser(possibleUserId.get());

        List<ItemResponse> response = new ArrayList<>();

        for (Item item : items) {
            response.add(new ItemResponse(item));
        }

        return ResponseEntity.ok(response);
    }

    // DELETE /users/{userId}
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid userId format");
        }

        Optional<User> possibleUser = userService.findUserById(possibleUserId.get());

        if (possibleUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        userService.deleteUser(possibleUserId.get());

        return ResponseEntity.ok("User deleted");
    }

    private Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}