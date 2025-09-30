package net.devoz.amexpilot.app.user;

import net.devoz.amexpilot.app.error.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class dbUserRepository implements UserRepository {

  private final ConcurrentHashMap<Long, User> USERS_STORE;
  private final AtomicLong idGenerator = new AtomicLong(0);
  private boolean found = false;

  public dbUserRepository() {
    this.USERS_STORE = new ConcurrentHashMap<>();
  }

  @Override
  public User save(User newUser) throws IllegalArgumentException {
    long id = idGenerator.incrementAndGet();
    User user = new User(id, newUser.name(), newUser.email());
    USERS_STORE.put(id, user);
    return user;
  }

  @Override
  public Optional<User> findById(long id) throws UserNotFoundException {
    User user = USERS_STORE.get(id);
    if(user == null) {
      throw new UserNotFoundException("User with id " + id + " not found. ");
    } else {
      return Optional.of(user);
    }
  }

  @Override
  public List<User> findAll() {
    return USERS_STORE.values().stream().toList();
  }

  @Override
  public void delete(long id) {
    if(id != 0) {
      USERS_STORE.remove(id);
    }
  }

  @Override
  public Optional<User> update(User user) throws UserNotFoundException, IllegalArgumentException {
    if(!USERS_STORE.containsKey(user.id())) {
      throw new UserNotFoundException("User does not exist for update.");
    } else {
      USERS_STORE.replace(user.id(), user);
      return Optional.of(user);
    }
  }

  @Override
  public int count() {
    return USERS_STORE.size();
  }

  private boolean containsUser(String name, String email) throws IllegalArgumentException {
    this.found = false;
    USERS_STORE.forEach((k,v) -> {
      if(v.email().contains(email) && v.name().contains(name)) {
        this.found = true;
      }
    });
    return this.found;
  }

}
