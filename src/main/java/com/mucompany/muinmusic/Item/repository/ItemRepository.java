package com.mucompany.muinmusic.Item.repository;

import com.mucompany.muinmusic.Item.domain.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id =:id")
    Optional<Item> findByIdWithPessimisticLock(Long id);
}
