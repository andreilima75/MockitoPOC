package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void testBasicMockAndStubbing() {
        when(repository.findById(1L)).thenReturn(new User(1L, "Andrei", "andrei@example.com"));

        User user = service.getUserById(1L);

        assertEquals("Andrei", user.getName());
        verify(repository).findById(1L);
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findById(anyLong())).thenReturn(new User());
        when(repository.save(any(User.class))).thenReturn(new User(99L, "Test", "test@email.com"));

        service.getUserById(999L);
        service.createUser("John", "john@email.com");

        verify(repository, times(1)).findById(anyLong());
        verify(repository).save(argThat(u -> u.getName().equals("John")));
    }

}