package com.mucompany.muinmusic.Item.repository;

import com.mucompany.muinmusic.Item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
}
