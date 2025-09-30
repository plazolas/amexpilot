package net.devoz.amexpilot.app.service;

import net.devoz.amexpilot.app.error.UserNotFoundException;
import net.devoz.amexpilot.app.user.User;
import net.devoz.amexpilot.app.user.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository dataStore) {
    this.userRepository = dataStore;
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public Optional<User> findById(Long id) throws UserNotFoundException {
    return userRepository.findById(id);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public void delete(long id) {
      userRepository.delete(id);
  }

  public Optional<User> update(User user) throws UserNotFoundException, IllegalArgumentException {
      return userRepository.update(user);
  }

  public int count() {
    return userRepository.count();
  }
}
