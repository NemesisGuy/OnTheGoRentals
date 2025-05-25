package za.ac.cput.service;

import za.ac.cput.domain.entity.Faq;

import java.util.List;
import java.util.UUID;

public interface IFaqService extends IService<Faq, Integer> {

    Faq read(UUID uuid);

    List<Faq> getAll();
}
