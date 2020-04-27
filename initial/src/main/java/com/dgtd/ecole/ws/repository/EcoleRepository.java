package com.dgtd.ecole.ws.repository;

import com.dgtd.rdc.entity.Ecole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoleRepository extends JpaRepository<Ecole, Integer> {

    @Query("select e from Ecole e where e.id = :id")
    Ecole getById(int id);

    /* to handle LazyInitializationException use JOIN FETCH directive
    Query query = session.createQuery(
            "from Model m " +
                    "join fetch m.modelType " +
                    "where modelGroup.id = :modelGroupId"
    );
    */
}
