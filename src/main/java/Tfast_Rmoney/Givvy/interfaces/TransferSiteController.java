package Tfast_Rmoney.Givvy.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Tfast_Rmoney.Givvy.entities.TransferSite;
import Tfast_Rmoney.Givvy.interfaces.dtos.CreateTransferSiteRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.TransferSiteResponse;
import Tfast_Rmoney.Givvy.services.TransferSiteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer-sites")
@CrossOrigin(origins = "*")
public class TransferSiteController {

    private final TransferSiteService transferSiteService;

    public TransferSiteController(TransferSiteService transferSiteService) {
        this.transferSiteService = transferSiteService;
    }

    // POST /transfer-sites
    @PostMapping
    public ResponseEntity<String> createTransferSite(@RequestBody CreateTransferSiteRequest request) {

        if (request.getName() == null || request.getName().isBlank()
                || request.getAddressOne() == null || request.getAddressOne().isBlank()
                || request.getCity() == null || request.getCity().isBlank()
                || request.getState() == null || request.getState().isBlank()
                || request.getZip() == null || request.getZip().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing name, address, city, state, or zip");
        }

        TransferSite site = new TransferSite();

        site.setName(request.getName());
        site.setAddressOne(request.getAddressOne());
        site.setAddressTwo(request.getAddressTwo());
        site.setCity(request.getCity());
        site.setState(request.getState());
        site.setZip(request.getZip());
        site.setImageUrl(request.getImageUrl());
        site.setDescription(request.getDescription());

        int id = transferSiteService.saveTransferSite(site);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(String.valueOf(id));
    }

    // GET /transfer-sites
    @GetMapping
    public ResponseEntity<List<TransferSiteResponse>> getAllTransferSites() {

        List<TransferSite> sites = transferSiteService.findAllTransferSites();

        List<TransferSiteResponse> response = new ArrayList<>();

        for (TransferSite site : sites) {
            response.add(new TransferSiteResponse(site));
        }

        return ResponseEntity.ok(response);
    }

    // GET /transfer-sites/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TransferSiteResponse> getTransferSiteById(@PathVariable int id) {

        Optional<TransferSite> possibleSite = transferSiteService.findTransferSiteById(id);

        if (possibleSite.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        TransferSiteResponse response = new TransferSiteResponse(possibleSite.get());

        return ResponseEntity.ok(response);
    }

    // DELETE /transfer-sites/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransferSite(@PathVariable int id) {

        boolean deleted = transferSiteService.deleteTransferSite(id);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transfer site not found");
        }

        return ResponseEntity.ok("Transfer site deleted");
    }
}
