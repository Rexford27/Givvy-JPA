package Tfast_Rmoney.Givvy.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.interfaces.dtos.ItemResponse;
import Tfast_Rmoney.Givvy.repositories.ItemRepository;
import Tfast_Rmoney.Givvy.repositories.UserRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public String saveItem(Item item, UUID donorId) {

        Optional<User> possibleDonor = userRepository.findById(donorId);

        if (possibleDonor.isEmpty()) {
            return "Invalid donor";
        }

        User donor = possibleDonor.get();

        item.setDonor(donor);
        item.setStatus("available");

        Item savedItem = itemRepository.save(item);

        return savedItem.getItemId().toString();
    }

    public Optional<Item> findItemById(UUID itemId) {
        return itemRepository.findById(itemId);
    }

    public List<Item> findItemsByUser(UUID userId) {

        Optional<User> possibleUser = userRepository.findById(userId);

        if (possibleUser.isEmpty()) {
            return List.of();
        }

        User user = possibleUser.get();

        List<Item> items = itemRepository.findByDonor(user);
        

        return items;
    }

    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> findItemsByStatus(String status) {
        return itemRepository.findByStatus(status);
    }

    public boolean updateStatus(UUID itemId, String status) {

        Optional<Item> possibleItem = itemRepository.findById(itemId);

        if (possibleItem.isEmpty()) {
            return false;
        }

        Item item = possibleItem.get();
        item.setStatus(status);

        itemRepository.save(item);

        return true;
    }

    public int updateStatus(UUID itemId, UUID donorId, String status) {

        // I use this secure version when the controller knows who is logged in.
        // Return meanings:
        // 1 = updated
        // -1 = item not found
        // -2 = logged-in user is not the donor

        Optional<Item> possibleItem = itemRepository.findById(itemId);

        if (possibleItem.isEmpty()) {
            return -1;
        }

        Item item = possibleItem.get();

        if (!itemBelongsToDonor(item, donorId)) {
            return -2;
        }

        item.setStatus(status);
        itemRepository.save(item);

        return 1;
    }

    public boolean cancelItem(UUID itemId) {

        if (!itemRepository.existsById(itemId)) {
            return false;
        }

        itemRepository.deleteById(itemId);

        return true;
    }

    public int cancelItem(UUID itemId, UUID donorId) {

        // I use this secure version when the controller knows who is logged in.
        // Return meanings:
        // 1 = deleted
        // -1 = item not found
        // -2 = logged-in user is not the donor

        Optional<Item> possibleItem = itemRepository.findById(itemId);

        if (possibleItem.isEmpty()) {
            return -1;
        }

        Item item = possibleItem.get();

        if (!itemBelongsToDonor(item, donorId)) {
            return -2;
        }

        itemRepository.deleteById(itemId);

        return 1;
    }

    private boolean itemBelongsToDonor(Item item, UUID donorId) {

        if (item.getDonor() == null || item.getDonor().getUserId() == null) {
            return false;
        }

        return item.getDonor().getUserId().equals(donorId);
    }
}