package pt.ulisboa.ewp.node.domain.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class EntityAuditListener {

  @Autowired private Logger log;

  @PostPersist
  private void onPostPersist(Object object) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronizationAdapter() {

          @Override
          public void afterCommit() {
            log.info(String.format("Persisted object: %n%s", object));
          }
        });
  }

  @PostUpdate
  private void onPostUpdate(Object object) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronizationAdapter() {

          @Override
          public void afterCommit() {
            log.info(String.format("Updated object: %n%s", object));
          }
        });
  }

  @PreRemove
  private void onPreRemove(Object object) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronizationAdapter() {

          @Override
          public void beforeCommit(boolean readOnly) {
            log.info(String.format("Removing object: %n%s", object));
          }
        });
  }

  @PostRemove
  private void onPostRemove(Object object) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronizationAdapter() {

          @Override
          public void afterCommit() {
            log.info(String.format("Removed object of type %s", object.getClass().getSimpleName()));
          }
        });
  }
}
