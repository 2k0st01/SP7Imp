package ua.kiev.prog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// DAO
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Modifying
    @Query("UPDATE Contact c SET c.group.id = :group_id WHERE c.id IN :user_ids")
    void moveToOtherGroup(@Param("user_ids") long[] userIds, @Param("group_id") long groupId);
}
