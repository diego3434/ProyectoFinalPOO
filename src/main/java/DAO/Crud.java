package DAO;

import java.util.List;

public interface Crud <T>{
    boolean guardar(T objeto);
    boolean actualizar(T objeto);
    boolean eliminar(int id);
    List<T> listar();
}
