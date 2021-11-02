package org.mvc.studentInit.repositorys;

import org.mvc.studentInit.model.Bid;
import org.mvc.studentInit.model.User;
import org.mvc.studentInit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingRepository extends JpaRepository<Vote, Integer> {

    @Query("select sum(v.votesFor) from Vote v where v.bid = :bid")
    Integer sumVotesFor(Bid bid);

    @Query("select sum(v.votesAgainst) from Vote v where v.bid = :bid")
    Integer sumVotesAgainst(Bid bid);

    //    @Query("select v from Vote v where v.bid = :bid and v.user = :user")
    Vote findVoteByUserAndBid(User user, Bid bid);

    List<Vote> findVoteByBid(Bid bid);
}