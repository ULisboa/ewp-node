package pt.ulisboa.ewp.node.utils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public class ClassUtils {

  private ClassUtils() {}

  /**
   * Determines the actual real class of a real object. If it is a proxy (e.g. Hibernate proxy) then
   * it resolves the proxy to obtain the actual class type.
   */
  public static Class<?> getRealClass(Object object) {
    if (object instanceof HibernateProxy) {
      return Hibernate.getClass(object);
    }
    return object.getClass();
  }
}
