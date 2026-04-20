package br.com.conectaPro.api.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> save(@RequestBody @Valid UserRequest request) {

        User user = userService.save(request.build());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable("id") Long id, @RequestBody UserRequest request) {

        userService.update(id, request.build());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    // Endereços

    @PostMapping("/address/{userId}")
    public ResponseEntity<AddressUser> postAddressUser(@PathVariable("userId") Long userId,
            @RequestBody @Valid AddressUserRequest request) {

        AddressUser address = userService.postAddressUser(userId, request.build());
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressUser> updateAddressUser(@PathVariable("addressId") Long addressId,
            @RequestBody AddressUserRequest request) {

        AddressUser address = userService.updateAddressUser(addressId, request.build());
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Void> deleteAddressUser(@PathVariable("addressId") Long addressId) {

        userService.deleteAddressUser(addressId);
        return ResponseEntity.noContent().build();
    }

}