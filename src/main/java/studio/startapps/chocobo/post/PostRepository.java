package studio.startapps.chocobo.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String>, PagingAndSortingRepository<Post, String> {

    Page<Post> findAllByAvailableTrue(Pageable pageable);

    Optional<Post> findByTitleIdAndAvailable(String titleId, boolean available);

    Optional<Post> findByTitleId(String titleId);

    boolean existsByTitleId(String titleId);

    @Query("{'$and': [{'$or': [{'title': { $regex: ?0, $options: 'i' }}, {'tags': { $regex: ?0, $options: 'i' }}, {'keywords': { $regex: ?0, $options: 'i' }}]}, {'available': true}]}")
    Page<Post> findByKeyword(String keyword, Pageable pageable);
}
