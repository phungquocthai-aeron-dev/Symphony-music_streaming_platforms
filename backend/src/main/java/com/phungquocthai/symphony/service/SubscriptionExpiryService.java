package com.phungquocthai.symphony.service;

import com.phungquocthai.symphony.constant.Role;
import com.phungquocthai.symphony.constant.SubscriptionStatus;
import com.phungquocthai.symphony.entity.Subscription;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.repository.SubscriptionRepository;
import com.phungquocthai.symphony.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SubscriptionExpiryService {

	@Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiredSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for (Subscription subscription : subscriptions) {
            if (subscription.getEnd_date().isBefore(LocalDate.now()) && !SubscriptionStatus.EXPIRED.getDisplayName().equals(subscription.getStatus())) {
                subscription.setStatus(SubscriptionStatus.EXPIRED.getDisplayName());
                subscriptionRepository.save(subscription);

                User user = subscription.getUser();
                user.setRole(Role.USER.getValue());
                userRepository.save(user);
            }
        }
    }
}