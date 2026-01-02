package com.Birthday_reminder.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.Birthday_reminder.dto.RelationshipPageDTO;
import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.mapper.RelationshipMapper;
import com.Birthday_reminder.service.RelationshipService;
import com.Birthday_reminder.vo.RelationshipPageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    private RelationshipMapper relationshipMapper;

    @Override
    public PageInfo<RelationshipPageVO> queryRelationshipPage(RelationshipPageDTO dto) {

        PageHelper.startPage(dto.getPage(), dto.getSize());


        List<Relationship> relationships = relationshipMapper.selectAll(dto);


        PageInfo<Relationship> pageInfo = new PageInfo<>(relationships);


        List<RelationshipPageVO> vos = pageInfo.getList().stream().map(relationship -> {
            RelationshipPageVO vo = new RelationshipPageVO();
            BeanUtils.copyProperties(relationship, vo);
            return vo;
        }).collect(Collectors.toList());


        PageInfo<RelationshipPageVO> result = new PageInfo<>(vos);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setSize(pageInfo.getSize());
        result.setStartRow(pageInfo.getStartRow());
        result.setEndRow(pageInfo.getEndRow());
        result.setPrePage(pageInfo.getPrePage());
        result.setNextPage(pageInfo.getNextPage());
        result.setIsFirstPage(pageInfo.isIsFirstPage());
        result.setIsLastPage(pageInfo.isIsLastPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setNavigatePages(pageInfo.getNavigatePages());
        result.setNavigatepageNums(pageInfo.getNavigatepageNums());
        result.setNavigateFirstPage(pageInfo.getNavigateFirstPage());
        result.setNavigateLastPage(pageInfo.getNavigateLastPage());

        return result;
    }

    @Override
    public Relationship getRelationship(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("无效的ID");
        }
        Relationship relationship = relationshipMapper.selectById(id);
        if (relationship == null) {
            throw new RuntimeException("未找到对应的亲友关系");
        }
        return relationship;
    }

    @Override
    public Relationship createRelationship(Relationship relationship) {
        int result = relationshipMapper.insert(relationship);
        return relationship;
    }

    @Override
    public Boolean updateRelationship(Relationship relationship) {
        Relationship existing = relationshipMapper.selectById(relationship.getId());
        if (existing == null) {
            return false;
        }
        int result = relationshipMapper.update(relationship);
        return result > 0;
    }

    @Override
    public Boolean deleteRelationship(Integer id) {
        int result = relationshipMapper.delete(id);
        return result > 0;
    }
}
