package br.com.conectaPro.model.user;

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
}
