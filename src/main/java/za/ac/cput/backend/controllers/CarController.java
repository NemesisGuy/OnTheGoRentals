package za.ac.cput.backend.controllers;
/**
 *  CarController.java
 *  This is the controller for the Car class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */
import za.ac.cput.domain.Car;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CarController {

    @GetMapping("/cars")
    public String cars() {
        List<Car> cars = new ArrayList<>();

        Car car1 = Car.builder()
                .id(123)
                .make("Toyota")
                .model("Corolla")
                .year(2021)
                .category("Sedan")
                .licensePlate("ABC123")
                .build();

        Car car2 = Car.builder()
                .id(234)
                .make("Ford")
                .model("Mustang")
                .year(2022)
                .category("Sports")
                .licensePlate("DEF456")
                .build();

        Car car3 = Car.builder()
                .id(345)
                .make("Honda")
                .model("Civic")
                .year(2020)
                .category("Sedan")
                .licensePlate("GHI789")
                .build();

        Car car4 = Car.builder()
                .id(456)
                .make("BMW")
                .model("X5")
                .year(2021)
                .category("SUV")
                .licensePlate("JKL012")
                .build();

        Car car5 = Car.builder()
                .id(567)
                .make("Chevrolet")
                .model("Camaro")
                .year(2023)
                .category("Sports")
                .licensePlate("MNO345")
                .build();

        cars.add(car1);
        cars.add(car2);
        cars.add(car3);
        cars.add(car4);
        cars.add(car5);

        StringBuilder sb = new StringBuilder();
        sb.append("<table>")
                .append("<tr><th>ID</th><th>Make</th><th>Model</th><th>Year</th><th>Category</th><th>License Plate</th></tr>");
        for (Car car : cars) {
            sb.append("<tr>")
                    .append("<td>").append(car.getId()).append("</td>")
                    .append("<td>").append(car.getMake()).append("</td>")
                    .append("<td>").append(car.getModel()).append("</td>")
                    .append("<td>").append(car.getYear()).append("</td>")
                    .append("<td>").append(car.getCategory()).append("</td>")
                    .append("<td>").append(car.getLicensePlate()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
}
