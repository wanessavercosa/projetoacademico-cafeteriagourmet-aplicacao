package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.ItemKit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemKitRepository extends JpaRepository<ItemKit, Long> {

    List<ItemKit> findByKitId(Long kitId);
}
