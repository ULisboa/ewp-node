package pt.ulisboa.ewp.node.domain.repository;

import com.google.common.base.Strings;
import java.util.Collection;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.KeyStoreConfiguration;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
public class KeyStoreConfigurationRepository extends AbstractRepository<KeyStoreConfiguration> {

  @Autowired @Lazy private MessageResolver messages;

  protected KeyStoreConfigurationRepository(SessionFactory sessionFactory) {
    super(KeyStoreConfiguration.class, sessionFactory);
  }

  public KeyStoreConfiguration getInstance() {
    Collection<KeyStoreConfiguration> entities = findAll();
    return entities.isEmpty() ? null : entities.iterator().next();
  }

  @Override
  protected boolean checkDomainConstraints(KeyStoreConfiguration entity) throws DomainException {
    if (findAll().stream().anyMatch(kc -> kc != entity && kc.getId() != entity.getId())) {
      throw new DomainException(messages.get("error.keystore.configuration.only.one.can.exist"));
    }

    if (Strings.isNullOrEmpty(entity.getCertificateAlias())) {
      throw new DomainException(
          messages.get("error.keystore.configuration.certificate.alias.must.be.defined"));
    }

    if (entity.getKeystore() == null) {
      throw new DomainException(
          messages.get("error.keystore.configuration.keystore.must.be.defined"));
    }

    return true;
  }
}
