package org.toughradius.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.toughradius.model.RadGroupMeta;
import org.toughradius.model.RadGroupMetaExample;
import org.toughradius.model.RadGroupMetaKey;

public interface RadGroupMetaMapper {
    int countByExample(RadGroupMetaExample example);

    int deleteByExample(RadGroupMetaExample example);

    int deleteByPrimaryKey(RadGroupMetaKey key);

    int insert(RadGroupMeta record);

    int insertSelective(RadGroupMeta record);

    List<RadGroupMeta> selectByExampleWithRowbounds(RadGroupMetaExample example, RowBounds rowBounds);

    List<RadGroupMeta> selectByExample(RadGroupMetaExample example);

    RadGroupMeta selectByPrimaryKey(RadGroupMetaKey key);

    int updateByExampleSelective(@Param("record") RadGroupMeta record, @Param("example") RadGroupMetaExample example);

    int updateByExample(@Param("record") RadGroupMeta record, @Param("example") RadGroupMetaExample example);

    int updateByPrimaryKeySelective(RadGroupMeta record);

    int updateByPrimaryKey(RadGroupMeta record);
}