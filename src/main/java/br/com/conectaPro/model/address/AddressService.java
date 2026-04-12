package br.com.conectaPro.model.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository repository;

    @Transactional
    public  Address save(Address address) {

        address.setEnabled(Boolean.TRUE);
        return repository.save(address);
    }

    public List<Address> getAll() {

        return repository.findAll();
    }

    public Address getById(Long id) {

        return repository.findById(id).get();
    }

    @Transactional
    public void update(Long id, Address addressChanged) {

        Address address = repository.findById(id).get();
        address.setStreet(addressChanged.getStreet());
        address.setNumber(addressChanged.getNumber());
        address.setNeighborhood(addressChanged.getNeighborhood());
        address.setCity(addressChanged.getCity());
        address.setState(addressChanged.getState());
        address.setZipCode(addressChanged.getZipCode());
        address.setLatitude(addressChanged.getLatitude());
        address.setLongitude(addressChanged.getLongitude());
        repository.save(address);
    }

    @Transactional
    public void delete(Long id) {

        Address address = repository.findById(id).get();
        address.setEnabled(Boolean.FALSE);

        repository.save(address);
    }
    
}
