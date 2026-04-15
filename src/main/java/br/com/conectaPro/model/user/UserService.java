package br.com.conectaPro.model.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    private final AddressUserRepository addressUserRepository;

    UserService(AddressUserRepository addressUserRepository) {
        this.addressUserRepository = addressUserRepository;
    }

    @Transactional
    public User save(User user) {

        user.setEnabled(Boolean.TRUE);
        return repository.save(user);
    }

    public List<User> getAll() {

        return repository.findAll();
    }

    public User getById(Long id) {

        return repository.findById(id).get();
    }

    @Transactional
    public void update(Long id, User userChanged) {

        User user = repository.findById(id).get();
        user.setName(userChanged.getName());
        user.setEmail(userChanged.getEmail());
        user.setPassword(userChanged.getPassword());
        user.setBirthDate(userChanged.getBirthDate());
        user.setPhone(userChanged.getPhone());
        user.setUserType(userChanged.getUserType());
        user.setRegistryId(userChanged.getRegistryId());
        user.setAddressId(userChanged.getAddressId());
        repository.save(user);
    }

    @Transactional
    public void delete(Long id) {

        User user = repository.findById(id).get();
        user.setEnabled(Boolean.FALSE);

        repository.save(user);
    }

    // Endereços

    @Transactional
    public AddressUser postAddressUser(Long userId, AddressUser address) {

        User user = this.getById(userId);

        address.setUserId(user);
        address.setEnabled(Boolean.TRUE);
        addressUserRepository.save(address);

        List<AddressUser> listAddressUser = user.getAddressId();

        if (listAddressUser == null) {
            listAddressUser = new ArrayList<AddressUser>();
        }

        listAddressUser.add(address);
        user.setAddressId(listAddressUser);
        repository.save(user);

        return address;
    }

    @Transactional
    public AddressUser updateAddressUser(Long id, AddressUser addressChanged) {

        AddressUser address = addressUserRepository.findById(id).get();
        address.setStreet(addressChanged.getStreet());
        address.setNumber(addressChanged.getNumber());
        address.setNeighborhood(addressChanged.getNeighborhood());
        address.setCity(addressChanged.getCity());
        address.setState(addressChanged.getState());
        address.setZipCode(addressChanged.getZipCode());
        address.setLatitude(addressChanged.getLatitude());
        address.setLongitude(addressChanged.getLongitude());
        address.setSupplement(addressChanged.getSupplement());

        return addressUserRepository.save(address);
    }

    @Transactional
    public void deleteAddressUser(Long idAddress) {

        AddressUser address = addressUserRepository.findById(idAddress).get();
        address.setEnabled(Boolean.FALSE);
        addressUserRepository.save(address);

        User user = this.getById(address.getUserId().getId());
        user.getAddressId().remove(address);
        repository.save(user);
    }

}
