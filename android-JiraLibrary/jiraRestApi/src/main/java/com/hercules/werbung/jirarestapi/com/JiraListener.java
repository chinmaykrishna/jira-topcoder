package com.hercules.werbung.jirarestapi.com;

/**
 * Interface of listener
 * @param <T>
 */
public interface JiraListener<T> {
    public void onResponse(T jiraObject);
    public void onErrorResponse(Exception e);
}
