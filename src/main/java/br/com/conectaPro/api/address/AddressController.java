package br.com.conectaPro.api.address;

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

import java.util.List;

import br.com.conectaPro.model.address.Address;
import br.com.conectaPro.model.address.AddressService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/address")
@CrossOrigin
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<Address> save(@RequestBody @Valid AddressRequest request) {

        Address address = addressService.save(request.build());
        return new ResponseEntity<Address>(address, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Address> getAll() {
        return addressService.getAll();
    }

    @GetMapping("/{id}")
    public Address getById(@PathVariable Long id) {
        return addressService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> update(@PathVariable("id") Long id, @RequestBody AddressRequest request) {

        addressService.update(id, request.build());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        addressService.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
