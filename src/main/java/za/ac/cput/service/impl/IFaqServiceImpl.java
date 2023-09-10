package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Faq;
import za.ac.cput.repository.IFaqRepository;
import za.ac.cput.service.IFaqService;

import java.util.ArrayList;

@Service("iFaqServiceImpl")
public class IFaqServiceImpl implements IFaqService {

    private static IFaqRepository repository;

    public IFaqServiceImpl(IFaqRepository repository) {
        this.repository = repository;
    }

    @Override
    public Faq create(Faq faq) {
        return this.repository.save(faq);
    }

    @Override
    public Faq read(Integer integer) {
        return this.repository.findById(integer).orElse(null);
    }

    @Override
    public Faq update(Faq faq) {
        if (this.repository.existsById(faq.getId()))
            return this.repository.save(faq);
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        if (this.repository.existsById(integer)) {
            this.repository.deleteById(integer);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Faq> getAll() {
        ArrayList<Faq> allFaq = (ArrayList<Faq>) this.repository.findAll();
        return allFaq;
    }
}
