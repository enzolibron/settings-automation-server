package com.caspo.settingsautomationserver.daos;

import com.caspo.settingsautomationserver.dtos.MarginDto;
import com.caspo.settingsautomationserver.models.Margin;
import com.caspo.settingsautomationserver.repositories.MarginRepository;
import com.caspo.settingsautomationserver.utils.DateUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@RequiredArgsConstructor
@Service
public class MarginDao {

    private final MarginRepository marginRepository;

    public List<Margin> getMarginsByGroupName(String groupName) {
        return marginRepository.findAllByMarginGroupName(groupName);
    }

    public List<MarginDto> getAllMargins() {
        return marginRepository.findAllByOrderByMarginGroupNameAsc().stream()
                .map(item -> {
                    MarginDto newMarginDto = new MarginDto();
                    newMarginDto.setBetTypeId(item.getBetTypeId());
                    newMarginDto.setBetTypeName(item.getBetTypeName());
                    newMarginDto.setSportId(item.getSportId());
                    newMarginDto.setId(item.getId());
                    newMarginDto.setIsRbMarket(item.getIsRbMarket());
                    newMarginDto.setMargin(item.getMargin());
                    newMarginDto.setMarginGroupName(item.getMarginGroupName());
                    newMarginDto.setMarketTypeId(item.getMarketTypeId());
                    newMarginDto.setCreated(DateUtil.formatDate(item.getCreated(), DateUtil.CORRECT_FORMAT));
                    newMarginDto.setModified(DateUtil.formatDate(item.getModified(), DateUtil.CORRECT_FORMAT));
                    return newMarginDto;
                }).collect(Collectors.toList());
    }

    public List<Margin> saveAll(List<Margin> margins) {
        return marginRepository.saveAll(margins);
    }

    public String deleteByGroupName(String groupName) {
        List<Margin> existing = getMarginsByGroupName(groupName);

        if (existing == null) {
            return null;
        } else {
            getMarginsByGroupName(groupName).stream().forEach(item -> marginRepository.delete(item));
            return "Deleted successfully.";
        }
    }

    public List<Margin> update(List<Margin> margins) {
        return margins.stream().map(margin -> {
            Optional<Margin> existing = marginRepository.findById(margin.getId());

            if (existing.isPresent()) {
                if (margin.getBetTypeId() != null) {
                    existing.get().setBetTypeId(margin.getBetTypeId());
                }

                if (margin.getBetTypeName() != null) {
                    existing.get().setBetTypeName(margin.getBetTypeName());
                }

                if (margin.getMargin() != null) {
                    existing.get().setMargin(margin.getMargin());
                }

                if (margin.getMarginGroupName() != null) {
                    existing.get().setMarginGroupName(margin.getMarginGroupName());
                }

                if (margin.getMarketTypeId() != null) {
                    existing.get().setMarketTypeId(margin.getMarketTypeId());
                }

                if (margin.getSportId() != null) {
                    existing.get().setSportId(margin.getSportId());
                }

                if (margin.getIsRbMarket() != null) {
                    existing.get().setIsRbMarket(margin.getIsRbMarket());
                }

                return marginRepository.save(existing.get());
            } else {
                return null;
            }

        }).collect(Collectors.toList());
    }

}
