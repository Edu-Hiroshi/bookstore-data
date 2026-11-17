package com.eduhi.bookstore.repositories;

import com.eduhi.bookstore.models.PublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PublisherRepository extends JpaRepository<PublisherEntity, UUID> {
}
