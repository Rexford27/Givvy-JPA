package Tfast_Rmoney.Givvy.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import Tfast_Rmoney.Givvy.entities.Interest;

public interface InterestRepository extends JpaRepository<Interest, Integer> {
    List<Interest> findByUserId(UUID userId);
    List<Interest> findByItemId(String itemId);
    //Express interest done by "save" method from JpaRepository
    //Deleting logic will require Rex's item functions to check status etc

}