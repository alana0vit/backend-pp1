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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.dto.UserResponseDTO;
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
        // Passando a lista de categorias e a entidade separados
        User user = userService.save(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable("id") Long id, @RequestBody UserRequest request) {

        userService.update(id, request.build(), request.getCategoriesIds());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    // Filtros de busca para user do tipo "PROFESSIONAL"

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radiusKm) {
        List<User> users = userService.search(name, categoryId, latitude, longitude, radiusKm);
        List<UserResponseDTO> response = users.stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Endereços

    @GetMapping("/{userId}/addresses")
    public List<AddressUser> getAllAddresses(@PathVariable Long userId) {
        return userService.getAllAddressByUser(userId);
    }

    @GetMapping("/addresses/{addressId}")
    public AddressUser getAddressById(@PathVariable Long addressId) {
        return userService.getAddressById(addressId);
    }

    @PostMapping("/{userId}/addresses")
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