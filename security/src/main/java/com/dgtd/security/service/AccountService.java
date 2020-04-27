package com.dgtd.security.service;

public interface AccountService<T> {
    public T saveUser(T user);

    public void addRoleToUser(String username, String rolename) throws SecurityException;

    public T findUserByUserName(String username);

    public void deleteUser(String username);

}
