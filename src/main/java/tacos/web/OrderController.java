package tacos.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.Order;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderProps props;

    @GetMapping("/current")
    public String orderForm(@AuthenticationPrincipal User user, @ModelAttribute Order order) {
        if(order.getDeliveryName() == null) {
            order.setDeliveryName(user.getFullname());
        }
        if(order.getDeliveryStreet() == null) {
            order.setDeliveryStreet(user.getStreet());
        }
        if(order.getDeliveryCity() == null) {
            order.setDeliveryCity(user.getCity());
        }
        if(order.getDeliveryState() == null) {
            order.setDeliveryState(user.getState());
        }
        if(order.getDeliveryZip() == null) {
            order.setDeliveryZip(user.getZip());
        }

        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus,
                               @AuthenticationPrincipal User user) {
        if(errors.hasErrors()) {
            return "orderForm";
        }

        order.setUser(user);

        orderRepository.save(order);
        sessionStatus.setComplete();

        return "redirect:/";
    }

    @GetMapping
    public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
        Pageable pageable = PageRequest.of(0, props.getPageSize());
        model.addAttribute("orders", orderRepository.findByUserOrderByPlacedAtDesc(user, pageable));

        return "orderList";
    }

    @PutMapping("/{orderId}")
    public Order putOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @PatchMapping(params = "/{orderId}", consumes = "application/json")
    public Order patchOrder(@PathVariable("orderId") Long orderId, @RequestBody Order patch) {
        Order order = orderRepository.findById(orderId).get();
        if(patch.getDeliveryCity() != null) {
            order.setDeliveryCity(patch.getDeliveryCity());
        }
        if(patch.getCcCVV() != null) {
            order.setCcCVV(patch.getCcCVV());
        }
        if(patch.getCcExpiration() != null) {
            order.setCcExpiration(patch.getCcExpiration());
        }
        if(patch.getCcNumber() != null) {
            order.setCcNumber(patch.getCcNumber());
        }
        if(patch.getDeliveryName() != null) {
            order.setDeliveryName(patch.getDeliveryName());
        }
        if(patch.getDeliveryState() != null) {
            order.setDeliveryState(patch.getDeliveryState());
        }
        if(patch.getDeliveryStreet() != null) {
            order.setDeliveryStreet(patch.getDeliveryStreet());
        }
        if(patch.getDeliveryZip() != null) {
            order.setDeliveryZip(patch.getDeliveryZip());
        }

        return orderRepository.save(order);
    }
}
