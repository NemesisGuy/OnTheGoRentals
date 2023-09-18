package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.repository.IHelpCenterRepository;
import za.ac.cput.service.IHelpCenterService;

import java.util.ArrayList;
import java.util.Optional;

@Service("iHelpCenterServiceImpl")
public class IHelpCenterServiceImpl implements IHelpCenterService {

    private static IHelpCenterRepository repository;

    public IHelpCenterServiceImpl(IHelpCenterRepository repository) {
        this.repository = repository;
    }

    @Override
    public HelpCenter create(HelpCenter helpCenter) {
//        HelpCenter helpCenter1 = helpCenterFactory.create(helpCenter);
//        return repository.save(helpCenter1);
        return this.repository.save(helpCenter);
    }

    @Override
    public HelpCenter read(Integer integer) {
        Optional<HelpCenter> helpCenter = this.repository.findById(integer);
        return helpCenter.orElse(null);
//        return this.repository.findById(id).orElse(null);
    }

    @Override
    public HelpCenter update(HelpCenter helpCenter) {
//        if (this.repository.existsById((int) helpCenter.getId())) {
//            HelpCenter helpCenter1 = helpCenterFactory.create(helpCenter);
//            return this.repository.save(helpCenter1);
//        }
//        return null;
        if (this.repository.existsById(helpCenter.getId())) {
            return this.repository.save(helpCenter);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<HelpCenter> getAll() {
        ArrayList<HelpCenter> allHelpCenter = (ArrayList<HelpCenter>) this.repository.findAll();
        return allHelpCenter;
    }

    public ArrayList<HelpCenter> getAllByCategory(String category) {
        return this.repository.findAllByCategory(category);
    }
}
