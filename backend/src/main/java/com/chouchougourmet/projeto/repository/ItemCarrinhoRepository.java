package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {

    List<ItemCarrinho> findByCarrinhoId(Long carrinhoId);
}

