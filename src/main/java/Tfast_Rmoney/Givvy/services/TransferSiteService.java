package Tfast_Rmoney.Givvy.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.entities.TransferSite;
import Tfast_Rmoney.Givvy.repositories.TransferSiteRepository;

@Service
public class TransferSiteService {

    private final TransferSiteRepository transferSiteRepository;

    public TransferSiteService(TransferSiteRepository transferSiteRepository) {
        this.transferSiteRepository = transferSiteRepository;
    }

    public int saveTransferSite(TransferSite site) {
        TransferSite savedSite = transferSiteRepository.save(site);
        return savedSite.getId();
    }

    public List<TransferSite> findAllTransferSites() {
        return transferSiteRepository.findAll();
    }

    public Optional<TransferSite> findTransferSiteById(int id) {
        return transferSiteRepository.findById(id);
    }

    public boolean deleteTransferSite(int id) {

        if (!transferSiteRepository.existsById(id)) {
            return false;
        }

        transferSiteRepository.deleteById(id);

        return true;
    }
}