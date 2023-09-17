package za.ac.cput.service.impl;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.*;
import za.ac.cput.factory.impl.DamageReportFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
class DamageReportServiceImplTest {
    @Autowired
    private DamageReportServiceImpl service;

    Car car = Car.builder().id(1).make("Toyota").model("Ayga").priceGroup(PriceGroup.ECONOMY).licensePlate("ACD256").build();
    User user = User.builder().id(2).userName("Ceedee").email("ceedee@gmail.com").pictureUrl("image-5.png").firstName("Cwenga").lastName("Dlova").phoneNumber("078 123 9236").password("Cee#2023").role("Customer").build();
    Rental rental = Rental.builder().setId(3).setUser(user).setCar(car).setIssuer(5).setReceiver(2).setFine(500).setIssuedDate(LocalDateTime.now()).setDateReturned(LocalDateTime.parse("2023-09-17T12:00:30")).build();
    DamageReport report1 = DamageReportFactory.createReport(1, rental, "Cracked right side mirror", LocalDateTime.parse("2023-09-17T12:50:00"),"Claremont", 850.00);

    @Test
    void a_Create(){
        DamageReport created = service.create(report1);
        System.out.println("Damage Report created: " + created);
        assertNotNull(created);
    }
    @Test
    void b_Read(){
        Optional<DamageReport> read = service.read(report1.getId());
        assertNotNull(read);
        System.out.println("Read Damage Report: " + read);

    }
    @Test
    void  c_Update(){
        DamageReport newReport = new DamageReport.Builder().copy(report1).setRepairCost(900.00).build();
        DamageReport updatedReport = service.update(newReport);
        assertEquals(newReport.getRepairCost(), updatedReport.getRepairCost());
        System.out.println("Updated report: " + updatedReport);

    }

    @Test
    void d_GetAll(){
        List<DamageReport> list = service.getAll();
        System.out.println("\nshow all: " + list);
    }

    @Test
    void d_Delete(){
        this.service.deleteById(report1.getId());
        List<DamageReport> damageReportList = this.service.getAll();
        assertEquals(0, damageReportList.size());
        /**    this.service.deleteById(address.getId());
         List<Address> addressList = this.service.findAll();
         assertEquals(0, addressList.size());
         }*/
    }
}