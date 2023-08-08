package pt.ulisboa.ewp.node.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "EWP_KEYSTORE_CONFIGURATION")
public class KeyStoreConfiguration {

  private long id;
  private byte[] keystore;
  private String certificateAlias;

  protected KeyStoreConfiguration() {}

  protected KeyStoreConfiguration(byte[] keystore, String certificateAlias) {
    this.keystore = keystore;
    this.certificateAlias = certificateAlias;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Lob
  @Column(name = "keystore", columnDefinition = "BLOB")
  public byte[] getKeystore() {
    return keystore;
  }

  public void setKeystore(byte[] keystore) {
    this.keystore = keystore;
  }

  @Column(name = "certificate_alias", nullable = false)
  public String getCertificateAlias() {
    return certificateAlias;
  }

  public void setCertificateAlias(String certificateAlias) {
    this.certificateAlias = certificateAlias;
  }

  public static KeyStoreConfiguration create(byte[] keystore, String certificateAlias) {
    return new KeyStoreConfiguration(keystore, certificateAlias);
  }

  @Override
  public String toString() {
    return String.format(
        "KeyStoreConfiguration(keystore length = %d bytes; certificateAlias = %s)",
        keystore.length, certificateAlias);
  }
}
