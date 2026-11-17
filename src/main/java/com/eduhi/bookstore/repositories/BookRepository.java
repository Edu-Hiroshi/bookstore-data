package com.eduhi.bookstore.repositories;

import com.eduhi.bookstore.dto.BooksProjection;
import com.eduhi.bookstore.models.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

    BookEntity findBookModelByTitle(String title);

    // example where you can create custom queries with nativeQuery
    @Query(value = "SELECT * FROM tb_book WHERE publisher_id = :id", nativeQuery = true)
    List<BookEntity> findBooksByPublisherId(@Param("id") UUID id);

    @NativeQuery(value = """
            SELECT b.id              AS id,
                   b.title           AS title,
                   p.name            AS publisher,
                   ARRAY_AGG(a.name) AS authors
            FROM TB_BOOK b
            INNER JOIN TB_PUBLISHER p
                ON b.publisher_id = p.id
            INNER JOIN TB_BOOK_AUTHOR ba
                ON ba.book_id = b.id
            INNER JOIN TB_AUTHOR a
                ON a.id = ba.author_id
            GROUP BY b.id, b.title, p.name;
            """)
    List<BooksProjection> getAllBooksWithPublisherAndAuthors();

    @NativeQuery(value = """
            SELECT b.id              AS id,
                   b.title           AS title,
                   p.name            AS publisher,
                   ARRAY_AGG(a.name) AS authors
            FROM TB_BOOK b
            INNER JOIN TB_PUBLISHER p
                ON b.publisher_id = p.id
            INNER JOIN TB_BOOK_AUTHOR ba
                ON ba.book_id = b.id
            INNER JOIN TB_AUTHOR a
                ON a.id = ba.author_id
            GROUP BY b.id, b.title, p.name;
            """,
        countQuery = """
            SELECT COUNT(b.id)
            FROM TB_BOOK b
            INNER JOIN TB_PUBLISHER p
                ON b.publisher_id = p.id
            INNER JOIN TB_BOOK_AUTHOR ba
                ON ba.book_id = b.id
            INNER JOIN TB_AUTHOR a
                ON a.id = ba.author_id
            GROUP BY b.id, b.title, p.name;
            """
    )
    Page<BooksProjection> getAllBooksWithPublisherAndAuthorsPage(Pageable pageable);
}
