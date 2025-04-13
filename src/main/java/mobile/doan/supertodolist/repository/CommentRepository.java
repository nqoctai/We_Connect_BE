package mobile.doan.supertodolist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mobile.doan.supertodolist.model.Comment;
import mobile.doan.supertodolist.model.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    Page<Comment> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);

    long countByPost(Post post);
}