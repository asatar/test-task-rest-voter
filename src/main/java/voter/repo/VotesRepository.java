package voter.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import voter.bean.Vote;

public interface VotesRepository extends JpaRepository<Vote,String> {
}
