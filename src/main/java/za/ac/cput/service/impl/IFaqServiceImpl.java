package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Faq;
import za.ac.cput.repository.IFaqRepository;
import za.ac.cput.service.IFaqService;

import java.util.List;
import java.util.UUID;

@Service("iFaqServiceImpl")
public class IFaqServiceImpl implements IFaqService {

    @Autowired
    private IFaqRepository repository;

    public IFaqServiceImpl(IFaqRepository repository) {
        this.repository = repository;
    }

    @Override
    public Faq create(Faq faq) {
        return repository.save(faq);
    }

    @Override
    public Faq read(Integer integer) {
        return repository.findByIdAndDeletedFalse(integer).orElse(null);
    }

    @Override
    public Faq read(UUID uuid) {
        return repository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    @Override
    public Faq update(Faq faq) {
        if (repository.existsById(faq.getId()))
            return repository.save(faq);
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        Faq faq = repository.findById(integer).orElse(null);
        if (faq != null && !faq.isDeleted()) {
            faq = new Faq.Builder().copy(faq).setDeleted(true).build();
            repository.save(faq);
            return true;
        }
        return false;
    }

    @Override
    public List<Faq> getAll() {
        List<Faq> allFaqs =  this.repository.findByDeletedFalse();
        return allFaqs;
    }
}
