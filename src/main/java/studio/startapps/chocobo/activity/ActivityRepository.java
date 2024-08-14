package studio.startapps.chocobo.activity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import studio.startapps.chocobo.activity.internal.ActivityActionEnum;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {

    int countByPostIdAndActionEquals(String postId, ActivityActionEnum action);
}
