package pt.ulisboa.ewp.node.service.ewp.notification.exception;

import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;

public class NoEwpCnrAPIException extends Exception {

  private final EwpChangeNotification changeNotification;

  public NoEwpCnrAPIException(
      EwpChangeNotification changeNotification) {
    this.changeNotification = changeNotification;
  }

  @Override
  public String getMessage() {
    return "No EWP CNR API is available for change notification: " + changeNotification;
  }
}
