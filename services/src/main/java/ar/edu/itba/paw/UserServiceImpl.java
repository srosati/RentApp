package ar.edu.itba.paw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    public User findById(String id) {
        return this.userDao.get(id);
    }
    public List<User> list() {
        return this.userDao.list();
    }
}