package com.caspo.settingsautomationserver.daos;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author 01PH1694.Lorenzo.L
 * @param <T>
 */
public interface Dao<T> {

    T get(Object id);

    List<T> getAll();

    T save(T t);

    T update(T t, Object param);

    String delete(Object id);
}
