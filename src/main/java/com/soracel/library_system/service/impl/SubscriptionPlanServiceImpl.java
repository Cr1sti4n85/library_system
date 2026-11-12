package com.soracel.library_system.service.impl;

import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.exception.PlanCodeException;
import com.soracel.library_system.mapper.SubscriptionPlanMapper;
import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;
import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.repository.SubscriptionPlanRepository;
import com.soracel.library_system.service.SubscriptionPlanService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository  subscriptionPlanRepository;
    private final UserService userService;

    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO spDto) {

        if (subscriptionPlanRepository.existsByPlanCode(spDto.getPlanCode())) {
            throw new PlanCodeException("Código del plan ingresado ya existe");
        }
        SubscriptionPlan newPlan = SubscriptionPlanMapper.toEntity(spDto);

        UserDTO currentUser = userService.getCurrentUser();

        newPlan.setCreatedBy(currentUser.getFullName());
        newPlan.setUpdatedBy(currentUser.getFullName());

        subscriptionPlanRepository.save(newPlan);
        return SubscriptionPlanMapper.toDTO(newPlan);
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO spDTO) {

        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId).orElse(null);
        if (existingPlan == null) {
            throw new NotFoundException("El plan de suscripción proporcionado no existe");
        }
        SubscriptionPlanMapper.updateEntityFromDTO(existingPlan, spDTO);
        UserDTO currentUser = userService.getCurrentUser();
        existingPlan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(existingPlan);
        return SubscriptionPlanMapper.toDTO(updatedPlan);
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId).orElse(null);
        if (existingPlan == null) {
            throw new NotFoundException("Plan no encontrado");
        }
        subscriptionPlanRepository.delete(existingPlan);

    }

    @Override
    public List<SubscriptionPlanDTO> getSubscriptionPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();

        return plans.stream().map(
                SubscriptionPlanMapper::toDTO
        ).toList();

    }

    @Override
    public SubscriptionPlan getSubscriptionPlanById(Long planId) {
        return subscriptionPlanRepository.findById(planId).orElseThrow(
                () -> new NotFoundException("Plan no encontrado")
        );

    }
}
