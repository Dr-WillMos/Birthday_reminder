package com.Birthday_reminder.mapper;

import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.dto.RelationshipPageDTO;

import java.util.List;

/**
* @author willm
* @description 针对表【relationship】的数据库操作Mapper
* @createDate 2025-12-14 14:38:30
* @Entity com.Birthday_reminder.domain.Relationship
*/
public interface RelationshipMapper {
    List<Relationship> selectAll(RelationshipPageDTO dto);

    Relationship selectById(Integer id);

    int insert(Relationship relationship);

    int update(Relationship relationship);

    int delete(Integer id);
}
