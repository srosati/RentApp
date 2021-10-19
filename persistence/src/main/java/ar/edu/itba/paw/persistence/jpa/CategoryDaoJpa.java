package ar.edu.itba.paw.persistence.jpa;

import ar.edu.itba.paw.interfaces.dao.CategoryDao;
import ar.edu.itba.paw.models.Category;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDaoJpa implements CategoryDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Category> listAll() {
        final TypedQuery<Category> query = em.createQuery("from Category as c where true", Category.class);
        return query.getResultList();
    }

    @Override
    public Optional<Category> findById(Long category) {
        return Optional.ofNullable(em.find(Category.class, category));
    }
}
