package org.example;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(force = true)
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getUserById(Long id) {
        return repository.findById(id);
    }

    public User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return repository.save(user);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    public User createUserWithRandomId(String andrei, String mail) {
        return new User(1L, UUID.randomUUID().toString(), "andrei.lima75@gmail.com");
    }
}