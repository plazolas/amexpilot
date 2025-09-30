package net.devoz.amexpilot.app.user;

import net.devoz.amexpilot.app.error.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
  User save(User user) throws IllegalArgumentException;
  Optional<User> findById(long id) throws UserNotFoundException;
  List<User> findAll();
  void delete(long id);
  Optional<User> update(User user) throws UserNotFoundException, IllegalArgumentException;
  int count();
}
