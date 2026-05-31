package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Spy
    private UserService spyService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

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

    @Test
    void testVoidMethods() {
        doNothing().when(repository).deleteById(anyLong());
        doThrow(new RuntimeException("DB Error")).when(repository).deleteById(999L);

        service.deleteUser(5L);
        assertThrows(RuntimeException.class, () -> service.deleteUser(999L));
    }

    @Test
    void testAdvancedStubbing() {
        when(repository.findById(1L))
                .thenReturn(new User(1L, "First", "a@b.com"))
                .thenThrow(new IllegalArgumentException("Not found"))
                .thenAnswer(invocation -> new User(invocation.getArgument(0), "Dynamic", "dyn@email.com"));

        assertEquals("First", service.getUserById(1L).getName());
        assertThrows(IllegalArgumentException.class, () -> service.getUserById(1L));
        assertEquals("Dynamic", service.getUserById(1L).getName());
    }

    @Test
    void testSpy() {
        doReturn(new User(1L, "Spy User", "spy@email.com"))
                .when(spyService).getUserById(1L);

        User user = spyService.getUserById(1L);
        assertEquals("Spy User", user.getName());
    }

    @Test
    void testBDDStyle() {
        given(repository.findById(10L)).willReturn(new User(10L, "BDD", "bdd@test.com"));

        User user = service.getUserById(10L);

        then(repository).should().findById(10L);
        assertNotNull(user);
    }

    @Test
    void testArgumentCaptor() {
        service.createUser("Captor Test", "captor@email.com");

        verify(repository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        assertEquals("Captor Test", captured.getName());
    }

    @Test
    void testVerification() {
        service.getUserById(1L);
        service.getUserById(1L);
        service.deleteUser(1L);

        verify(repository, times(2)).findById(1L);
        verify(repository, atLeastOnce()).deleteById(anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    void testStaticMock() {
        UUID expectedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        try (MockedStatic<UUID> mockedStatic = mockStatic(UUID.class)) {

            mockedStatic.when(UUID::randomUUID)
                    .thenReturn(expectedUUID);

            User user = service.createUserWithRandomId("Andrei", "andrei@test.com");

            assertEquals(expectedUUID.toString(), user.getName());
        }
    }

    @Test
    void testMockConstructor() {
        try (MockedConstruction<User> mocked = mockConstruction(User.class,
                (mock, context) -> {
                    when(mock.getName()).thenReturn("Constructed User");
                })) {

            User user = new User();
            assertEquals("Constructed User", user.getName());
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    void testCustomAnswer() {
        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(42L);
            return u;
        });

        User saved = service.createUser("Answer", "answer@test.com");
        assertEquals(42L, saved.getId());
    }

    @Test
    void testReset() {
        when(repository.findById(1L)).thenReturn(new User());
        reset(repository);
    }

}