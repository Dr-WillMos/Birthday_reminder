package com.Birthday_reminder.service;

import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.dto.RelationshipPageDTO;
import com.Birthday_reminder.vo.RelationshipPageVO;
import com.github.pagehelper.PageInfo;
import com.Birthday_reminder.dto.RelationshipPageDTO;
import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.vo.RelationshipPageVO;
import com.Birthday_reminder.vo.ResponseEntity;

public interface RelationshipService {
    PageInfo<RelationshipPageVO> queryRelationshipPage(RelationshipPageDTO dto);

    Relationship getRelationship(Integer id);

    Relationship createRelationship(Relationship relationship);

    Boolean updateRelationship(Relationship relationship);

    Boolean deleteRelationship(Integer id);
}