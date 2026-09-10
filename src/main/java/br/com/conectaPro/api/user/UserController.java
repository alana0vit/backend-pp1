package br.com.conectaPro.api.user;

import br.com.conectaPro.dto.UserResponseDTO;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
@Tag(name = "User/Address")
public class UserController {
  @Autowired private UserService userService;

  @Operation(summary = "Criar entidade usuário")
  @PostMapping
  public ResponseEntity<User> save(@RequestBody @Valid UserRequest request) {
    User user = userService.save(request);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @Operation(summary = "Lista todos os usuarios")
  @GetMapping
  public ResponseEntity<List<UserResponseDTO>> getAll() {
    List<UserResponseDTO> response =
        userService.getAll().stream().map(UserResponseDTO::fromEntity).toList();
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Lista um usuario especifico pelo seu ID")
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
    User user = userService.getById(id);
    return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
  }

  @Operation(summary = "Atualiza um usuario pelo seu ID")
  @PutMapping("/{id}")
  public ResponseEntity<User> update(
      @PathVariable("id") Long id, @RequestBody UserRequest request) {

    userService.update(id, request);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Deleta um usuario pelo seu ID")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {

    userService.delete(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Envia/atualiza a foto de um usuario")
  @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponseDTO> updatePhoto(
      @PathVariable Long id, @RequestParam("foto") MultipartFile foto) {

    User user = userService.updatePhoto(id, foto);
    return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
  }

  @Operation(summary = "Remove a foto de um usuario")
  @DeleteMapping("/{id}/photo")
  public ResponseEntity<UserResponseDTO> deletePhoto(@PathVariable Long id) {
    User user = userService.deletePhoto(id);
    return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
  }

  @Operation(summary = "Filtro de busca por profissional")
  @GetMapping("/search")
  public ResponseEntity<List<UserResponseDTO>> search(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Double latitude,
      @RequestParam(required = false) Double longitude,
      @RequestParam(required = false) Double radiusKm) {
    List<User> users = userService.search(name, categoryId, latitude, longitude, radiusKm);
    List<UserResponseDTO> response = users.stream().map(UserResponseDTO::fromEntity).toList();
    return ResponseEntity.ok(response);
  }

  // Endereços

  @Operation(summary = "Lista todos os endereços de um usuario")
  @GetMapping("/{userId}/addresses")
  public List<AddressUser> getAllAddresses(@PathVariable Long userId) {
    return userService.getAllAddressByUser(userId);
  }

  @Operation(summary = "Lista um endereço especifico")
  @GetMapping("/addresses/{addressId}")
  public AddressUser getAddressById(@PathVariable Long addressId) {
    return userService.getAddressById(addressId);
  }

  @Operation(summary = "Criar endereço para um usuario")
  @PostMapping("/{userId}/addresses")
  public ResponseEntity<AddressUser> postAddressUser(
      @PathVariable("userId") Long userId, @RequestBody @Valid AddressUserRequest request) {

    AddressUser address = userService.postAddressUser(userId, request.build());
    return new ResponseEntity<>(address, HttpStatus.CREATED);
  }

  @Operation(summary = "Atualiza um endereço pelo seu ID")
  @PutMapping("/address/{addressId}")
  public ResponseEntity<AddressUser> updateAddressUser(
      @PathVariable("addressId") Long addressId, @RequestBody AddressUserRequest request) {

    AddressUser address = userService.updateAddressUser(addressId, request.build());
    return new ResponseEntity<>(address, HttpStatus.OK);
  }

  @Operation(summary = "Deleta um endereço pelo seu ID")
  @DeleteMapping("/address/{addressId}")
  public ResponseEntity<Void> deleteAddressUser(@PathVariable("addressId") Long addressId) {

    userService.deleteAddressUser(addressId);
    return ResponseEntity.noContent().build();
  }
}
