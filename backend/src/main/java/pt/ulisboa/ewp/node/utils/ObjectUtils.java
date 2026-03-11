package pt.ulisboa.ewp.node.utils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public class ObjectUtils {

  private ObjectUtils() {}

  /** Returns the real object, free of proxies (e.g. Hibernate proxy). */
  public static Object getRealObject(Object object) {
    if (object instanceof HibernateProxy) {
      return Hibernate.unproxy(object);
    }
    return object;
  }
}
