package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.repository.IHelpCenterRepository;
import za.ac.cput.service.IHelpCenterService;

import java.util.ArrayList;
import java.util.Optional;

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
        Optional<HelpCenter> helpCenter = repository.findById(integer);
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
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<HelpCenter> getAll() {
        ArrayList<HelpCenter> allHelpCenter = (ArrayList<HelpCenter>) repository.findAll();
        return allHelpCenter;
    }

    public ArrayList<HelpCenter> getAllByCategory(String category) {
        return repository.findAllByCategory(category);
    }
}
