package Tfast_Rmoney.Givvy.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Tfast_Rmoney.Givvy.entities.Interest;

public interface InterestRepository extends JpaRepository<Interest, Integer> {
    @Query("SELECT i FROM Interest i WHERE i.user.userId = :userId")
    Optional<List<Interest>> findByUserId(UUID userId);
    @Query("SELECT i FROM Interest i WHERE i.item.itemId = :itemId")
    Optional<List<Interest>> findByItemId(UUID itemId);
    //Express interest done by "save" method from JpaRepository
    //Deleting logic will require Rex's item functions to check status etc

}