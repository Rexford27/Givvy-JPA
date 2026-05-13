package Tfast_Rmoney.Givvy.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.User;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findByDonor(User donor);

    List<Item> findByStatus(String status);
}