package studio.startapps.chocobo.activity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import studio.startapps.chocobo.activity.internal.ActivityActionEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {

    int countByPostIdAndActionEquals(String postId, ActivityActionEnum action);

    @Query(value = "{'action': ?0, 'remoteAddr': ?1, 'performedOn': { $gte: ?2 }}", count = true)
    long countByRemoteAddrSinceDate(ActivityActionEnum action, String remoteAddr, LocalDateTime minDate);
}
