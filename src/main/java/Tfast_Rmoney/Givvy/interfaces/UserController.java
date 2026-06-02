package Tfast_Rmoney.Givvy.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.interfaces.dtos.ItemResponse;
import Tfast_Rmoney.Givvy.interfaces.dtos.LoginRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.RegisterUserRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.UserResponse;
import Tfast_Rmoney.Givvy.security.AuctionUserDetails;
import Tfast_Rmoney.Givvy.security.JwtService;
import Tfast_Rmoney.Givvy.services.ItemService;
import Tfast_Rmoney.Givvy.services.UserService;
import Tfast_Rmoney.Givvy.security.JwtService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final ItemService itemService;
    private final JwtService jwtService;

    

    public UserController(
            UserService userService,
            ItemService itemService,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.itemService = itemService;
        this.jwtService = jwtService;
    }

    // POST /users/login
    @PostMapping("/login")
    public ResponseEntity<String> checkLogin(@RequestBody LoginRequest request) {

        // I do not use Authentication here because the user is not logged in yet.
        // This is the route where they prove who they are with email and password.

        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Empty email or password");
        }

        // I ask the service to check if the email and password match a real user.
        Optional<User> result = userService.loginAndReturnUser(
                request.getEmail(),
                request.getPassword()
        );

        // If the service cannot find a matching user, I reject the login.
        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        // If login is successful, I create a JWT using this user's id.
        // This token is what the user will send on future protected requests.
        String token = jwtService.makeJwt(result.get().getUserId().toString());

        // I return the token instead of returning just the user id.
        return ResponseEntity.ok(token);
    }

    // POST /users
    @PostMapping
    public ResponseEntity<String> save(@RequestBody RegisterUserRequest request) {

        // I do not use Authentication here because a new user does not have a token yet.
        // This route is public because it creates the account.

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

        // I am keeping the same pattern from my original controller.
        // Later, I should make sure the password is being hashed before it is saved.
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
    public ResponseEntity<UserResponse> getUserById(
            Authentication authentication,
            @PathVariable String userId
    ) {

        // I keep the same route, but now I also require Authentication.
        // The userId from the URL must match the userId inside the JWT.

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        // I get the logged-in user's id from the JWT token.
        UUID loggedInUserId = getLoggedInUserId(authentication);

        // I only allow the user to look up their own account.
        // If the URL id does not match the token id, I block the request.
        if (!loggedInUserId.equals(possibleUserId.get())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
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
    public ResponseEntity<List<ItemResponse>> getItemsByUser(
            Authentication authentication,
            @PathVariable String userId
    ) {

        // I keep the same route, but I secure it with the logged-in user's token.
        // The user can only request items for their own userId.

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        // I get the user id from the Authentication object, not from the request body.
        UUID loggedInUserId = getLoggedInUserId(authentication);

        // I compare the URL userId to the logged-in user's id from the JWT.
        if (!loggedInUserId.equals(possibleUserId.get())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
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
    public ResponseEntity<String> deleteUser(
            Authentication authentication,
            @PathVariable String userId
    ) {

        // I keep the same delete route, but I make sure users can only delete themselves.
        // The URL userId must match the userId inside the JWT token.

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid userId format");
        }

        // I get the logged-in user's id from the security token.
        UUID loggedInUserId = getLoggedInUserId(authentication);

        // If someone tries to delete another user's account, I return 403 Forbidden.
        if (!loggedInUserId.equals(possibleUserId.get())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can only delete your own account");
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

    private UUID getLoggedInUserId(Authentication authentication) {

        // I use authentication.getPrincipal() to get the logged-in user's identity.
        // In this project, that identity is stored as an AuctionUserDetails object.

        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        // In this project, getUsername() is really storing the user's UUID as a String.
        // I convert it back into a UUID so I can compare it with ids from the URL or database.

        return UUID.fromString(details.getUsername());
    }

    private Optional<UUID> parseUuid(String value) {

        // I use this helper so my controller does not crash if someone sends a bad UUID.

        try {
            return Optional.of(UUID.fromString(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}