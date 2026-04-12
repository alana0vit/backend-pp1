package br.com.conectaPro.model.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

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
        user.setActive(userChanged.getActive());

        repository.save(user);
    }

    @Transactional
    public void delete(Long id) {

        User user = repository.findById(id).get();
        user.setEnabled(Boolean.FALSE);

        repository.save(user);
    }

}
