package com.houjun.domain;


public class Memory_by_thread_by_current_bytes {
    private String thread_id;
    private String user;
    private String current_allocated;

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCurrent_allocated() {
        return current_allocated;
    }

    public void setCurrent_allocated(String current_allocated) {
        this.current_allocated = current_allocated;
    }
}
