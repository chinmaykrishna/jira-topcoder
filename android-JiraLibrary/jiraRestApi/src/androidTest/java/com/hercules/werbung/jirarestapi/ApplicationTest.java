package com.hercules.werbung.jirarestapi;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.hercules.werbung.jirarestapi.com.JiraListener;
import com.hercules.werbung.jirarestapi.json.meta.CreateMeta;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testGetCreateMeta() {
        JiraRestClient jiraRestClient = JiraRestClient.getInstance(getContext());
        jiraRestClient.setBaseURL("https://adqa-dev.atlassian.net");
        final CreateMeta jiraObj;

        final Object lock = new Object();

        jiraRestClient.getCreateMeta("topcoder", "pass1234", new JiraListener<CreateMeta>() {
            @Override
            public void onResponse(CreateMeta jiraObject) {
                org.junit.Assert.assertNotNull("GetCreateMeta without parameter", jiraObject);
                synchronized (lock) {
                    lock.notify();
                }
            }

            @Override
            public void onErrorResponse(Exception e) {
                org.junit.Assert.assertNotNull("GetCreateMeta without parameter", null);
                synchronized (lock) {
                    lock.notify();
                }
            }
        }, null, null, null, null);


        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testGetIssue() {
        JiraRestClient jiraRestClient = JiraRestClient.getInstance(getContext());
        jiraRestClient.setBaseURL("https://adqa-dev.atlassian.net");

        final Object lock = new Object();

        jiraRestClient.getIssue("topcoder", "pass1234",
                "10001",
                null,
                null,
                new JiraListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jiraObject) {
                        org.junit.Assert.assertNotNull("GetIssue", jiraObject);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        org.junit.Assert.assertNotNull("GetIssue", null);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                });


        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testSearch() {
        JiraRestClient jiraRestClient = JiraRestClient.getInstance(getContext());
        jiraRestClient.setBaseURL("https://adqa-dev.atlassian.net");
        final CreateMeta jiraObj;

        final Object lock = new Object();

        jiraRestClient.search("topcoder", "pass1234",
                null,
                null,
                null,
                null,
                null,
                null,
                new JiraListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jiraObject) {
                        org.junit.Assert.assertNotNull("GetCreateMeta without parameter", jiraObject);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        org.junit.Assert.assertNotNull("GetCreateMeta without parameter", null);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                });


        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testGetTabByScreenId() {
        JiraRestClient jiraRestClient = JiraRestClient.getInstance(getContext());
        jiraRestClient.setBaseURL("https://adqa-dev.atlassian.net");

        final Object lock = new Object();

        jiraRestClient.getTabsByScreenId("topcoder", "pass1234",
                "10003",
                null,
                new JiraListener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jiraObject) {
                        org.junit.Assert.assertNotNull("GetTabByScreenId", jiraObject);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        org.junit.Assert.assertNotNull("GetTabByScreenId", null);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                });


        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testGetFieldByTabId() {
        JiraRestClient jiraRestClient = JiraRestClient.getInstance(getContext());
        jiraRestClient.setBaseURL("https://adqa-dev.atlassian.net");

        final Object lock = new Object();

        jiraRestClient.getFieldsByTabId("topcoder", "pass1234",
                "10003",
                "10301",
                null,
                new JiraListener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jiraObject) {
                        org.junit.Assert.assertNotNull("GetFieldByTabId", jiraObject);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        org.junit.Assert.assertNotNull("GetFieldByTabId", null);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                });


        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}