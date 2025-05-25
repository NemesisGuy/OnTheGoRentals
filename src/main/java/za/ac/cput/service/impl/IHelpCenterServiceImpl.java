package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.HelpCenter;
import za.ac.cput.repository.IHelpCenterRepository;
import za.ac.cput.service.IHelpCenterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("iHelpCenterServiceImpl")
public class IHelpCenterServiceImpl implements IHelpCenterService {
    @Autowired
    private IHelpCenterRepository repository;

    public IHelpCenterServiceImpl(IHelpCenterRepository repository) {
        this.repository = repository;
    }

    @Override
    public HelpCenter create(HelpCenter helpCenter) {

        return repository.save(helpCenter);
    }

    @Override
    public HelpCenter read(Integer integer) {
        Optional<HelpCenter> helpCenter = repository.findByIdAndDeletedFalse(integer);
        return helpCenter.orElse(null);
    }

    @Override
    public HelpCenter update(HelpCenter helpCenter) {
        if (repository.existsById(helpCenter.getId())) {
            return repository.save(helpCenter);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        HelpCenter helpCenter = repository.findById(id).orElse(null);
        if (helpCenter != null && !helpCenter.isDeleted()) {
            helpCenter = new HelpCenter.Builder().copy(helpCenter).setDeleted(true).build();
            repository.save(helpCenter);
            return true;
        }
        return false;
    }

    @Override
    public List<HelpCenter> getAll() {
        List<HelpCenter> allHelpCenters =  repository.findByDeletedFalse();
        return allHelpCenters;
    }

    @Override
    public List<HelpCenter> findByCategory(String category) {
        return repository.findByCategoryAndDeletedFalse(category);
    }

    @Override
    public List<HelpCenter> read(String category) {
        return repository.findByCategoryAndDeletedFalse(category);
    }

    @Override
    public HelpCenter read(UUID uuid) {
        return repository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    public ArrayList<HelpCenter> getAllByCategory(String category) {
        return repository.findAllByCategoryAndDeletedFalse(category);
    }
}
