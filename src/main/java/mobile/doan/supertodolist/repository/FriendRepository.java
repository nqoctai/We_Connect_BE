package mobile.doan.supertodolist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mobile.doan.supertodolist.model.Friend;
import mobile.doan.supertodolist.model.Friend.FriendStatus;
import mobile.doan.supertodolist.model.User;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, JpaSpecificationExecutor<Friend> {

        List<Friend> findBySenderAndStatus(User sender, FriendStatus status);

        List<Friend> findByReceiverAndStatus(User receiver, FriendStatus status);

        // Changed from Optional to List to handle multiple records
        List<Friend> findBySenderAndReceiver(User sender, User receiver);

        boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendStatus status);

        List<Friend> findByReceiverIdAndStatus(long receiverId, FriendStatus status);

        List<Friend> findBySenderIdOrReceiverIdAndStatus(long senderId, long receiverId, FriendStatus status);

        @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f WHERE " +
                        "((f.sender.id = :userId1 AND f.receiver.id = :userId2) OR " +
                        "(f.sender.id = :userId2 AND f.receiver.id = :userId1)) AND " +
                        "f.status = 'ACCEPTED'")
        boolean areFriends(@Param("userId1") long userId1, @Param("userId2") long userId2);

        @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f WHERE " +
                        "f.sender.id = :senderId AND f.receiver.id = :receiverId AND f.status = 'PENDING'")
        boolean hasRequestSent(@Param("senderId") long senderId, @Param("receiverId") long receiverId);

        @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f WHERE " +
                        "f.sender.id = :otherUserId AND f.receiver.id = :currentUserId AND f.status = 'PENDING'")
        boolean hasRequestReceived(@Param("currentUserId") long currentUserId, @Param("otherUserId") long otherUserId);

        Friend findBySenderAndReceiverAndStatus(User sender, User receiver, FriendStatus status);
}