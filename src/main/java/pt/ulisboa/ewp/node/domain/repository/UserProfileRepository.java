package pt.ulisboa.ewp.node.domain.repository;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import pt.ulisboa.ewp.node.domain.entity.user.UserProfile;
import pt.ulisboa.ewp.node.domain.entity.user.UserProfile_;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

import com.google.common.base.Strings;

@Repository
@Transactional
public class UserProfileRepository extends AbstractRepository<UserProfile> {

  @Autowired private Logger log;

  @Autowired @Lazy private MessageResolver messages;

  protected UserProfileRepository(SessionFactory sessionFactory) {
    super(UserProfile.class, sessionFactory);
  }

  public Optional<UserProfile> findByUsername(String username) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<UserProfile> query = criteriaBuilder.createQuery(UserProfile.class);
          Root<UserProfile> selection = query.from(UserProfile.class);
          return Optional.ofNullable(
              session
                  .createQuery(
                      query.where(
                          criteriaBuilder.equal(selection.get(UserProfile_.username), username)))
                  .getSingleResult());
        });
  }

  @Override
  protected boolean checkDomainConstraints(UserProfile userProfile) throws DomainException {
    if (findAll().stream()
        .anyMatch(
            up ->
                up != userProfile && Objects.equals(up.getUsername(), userProfile.getUsername()))) {
      throw new DomainException(
          messages.get("error.user.profile.username.already.exists", userProfile.getUsername()));
    }

    if (Strings.isNullOrEmpty(userProfile.getUsername())) {
      throw new DomainException(messages.get("error.user.profile.username.must.be.defined"));
    }

    if (userProfile.getRole() == null) {
      throw new DomainException(messages.get("error.user.profile.role.must.be.defined"));
    }

    return true;
  }
}
