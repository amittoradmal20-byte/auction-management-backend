package com.auction.cricketgroup.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.cricketgroup.dto.CricketGroupCreateRequest;
import com.auction.cricketgroup.dto.CricketGroupResponse;
import com.auction.cricketgroup.entity.CricketGroup;
import com.auction.cricketgroup.enums.CricketGroupStatus;
import com.auction.cricketgroup.mapper.CricketGroupMapper;
import com.auction.cricketgroup.repository.CricketGroupRepository;

@Service
@Transactional
public class CricketGroupServiceImpl implements CricketGroupService {

    private final CricketGroupRepository cricketGroupRepository;
    private final CricketGroupMapper cricketGroupMapper;
    
	public CricketGroupServiceImpl(CricketGroupRepository cricketGroupRepository, CricketGroupMapper cricketGroupMapper) {
		this.cricketGroupRepository = cricketGroupRepository;
		this.cricketGroupMapper = cricketGroupMapper;
	}


    @Override
    public CricketGroupResponse createGroup(
            CricketGroupCreateRequest request) {

        if (cricketGroupRepository
                .existsByNameIgnoreCase(request.getName().trim())) {

            throw new IllegalArgumentException(
                    "Cricket group with this name already exists");
        }

        CricketGroup cricketGroup = new CricketGroup();

        cricketGroup.setName(request.getName().trim());
        cricketGroup.setDescription(
                request.getDescription() != null
                        ? request.getDescription().trim()
                        : null);
        cricketGroup.setStatus(CricketGroupStatus.ACTIVE);

        CricketGroup savedGroup =
                cricketGroupRepository.save(cricketGroup);

        return cricketGroupMapper.toResponse(savedGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public CricketGroupResponse getGroupById(
            UUID groupId) {

        CricketGroup cricketGroup =
                cricketGroupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cricket group not found"));

        return cricketGroupMapper.toResponse(cricketGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CricketGroupResponse> getAllGroups() {

        return cricketGroupRepository.findAll()
                .stream()
                .map(cricketGroupMapper::toResponse)
                .toList();
    }
}
