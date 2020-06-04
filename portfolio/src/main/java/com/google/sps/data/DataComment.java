package com.google.sps.data;

/** Class containing comments. */
public final class DataComment {
  
  private final long id;
  private final String creator;
  private final String message;
  private final String dateCreated;

  public DataComment(long id, String creator, String message, String dateCreated) {
    this.id = id;
    this.creator = creator;
    this.message = message;
    this.dateCreated = dateCreated;
  }

  public String getCreator() {
    return creator;
  }

  public String getMessage() {
    return message;
  }

  public String getDateCreated() {
    return dateCreated;
  }

}
