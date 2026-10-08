package com.medlitai.repository;

import com.medlitai.model.Literature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiteratureRepository extends JpaRepository<Literature, Long> {
    
    List<Literature> findByTopicIgnoreCase(String topic);

    @Query("SELECT l FROM Literature l WHERE " +
           "LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.abstractText) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.topic) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Literature> searchByKeyword(@Param("keyword") String keyword);
}
