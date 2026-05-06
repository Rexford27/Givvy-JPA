package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;

import Tfast_Rmoney.Givvy.core.TransferSite;
import Tfast_Rmoney.Givvy.core.TransferSiteDAO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer-sites")
@CrossOrigin(origins = "*")
public class TransferSiteController {

    private TransferSiteDAO dao;

    public TransferSiteController(TransferSiteDAO dao) {
        this.dao = dao;
    }

    // POST /transfer-sites
    // Creates a new safe transfer location.
    @PostMapping
    public ResponseEntity<String> createTransferSite(@RequestBody TransferSite site) {

        // Basic validation so we do not save an empty location.
        if (site.getName() == null || site.getName().isBlank()
                || site.getAddressOne() == null || site.getAddressOne().isBlank()
                || site.getCity() == null || site.getCity().isBlank()
                || site.getState() == null || site.getState().isBlank()
                || site.getZip() == null || site.getZip().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing name, address, city, state, or zip");
        }

        int rows = dao.save(site);

        if (rows == 0) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not create transfer site");
        }

        return ResponseEntity.ok("Transfer site created");
    }

    // GET /transfer-sites
    // Returns every transfer site.
    @GetMapping
    public ResponseEntity<List<TransferSite>> getAllTransferSites() {
        return ResponseEntity.ok(dao.findAll());
    }

    // GET /transfer-sites/{id}
    // Returns one transfer site by id.
    @GetMapping("/{id}")
    public ResponseEntity<TransferSite> getTransferSiteById(@PathVariable int id) {
        TransferSite site = dao.findById(id);

        if (site == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(site);
    }

    // DELETE /transfer-sites/{id}
    // Optional admin feature.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransferSite(@PathVariable int id) {
        int rows = dao.delete(id);

        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transfer site not found");
        }

        return ResponseEntity.ok("Transfer site deleted");
    }
}