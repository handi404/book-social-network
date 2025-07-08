package com.hd.book.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    @Query("""
           select book
           from Book book
           where book.archived = false
           and book.shareable = true
           and book.createdBy != :userId
           """)
    Page<Book> findAllDisplayableBooks(PageRequest pageRequest, @Param("userId") Integer id);

}
