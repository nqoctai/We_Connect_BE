package mobile.doan.supertodolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mobile.doan.supertodolist.model.Like;
import mobile.doan.supertodolist.model.Post;
import mobile.doan.supertodolist.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Like findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

    long countByPost(Post post);
}