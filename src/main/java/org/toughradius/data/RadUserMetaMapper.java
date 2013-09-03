package org.toughradius.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.toughradius.model.RadUserMeta;
import org.toughradius.model.RadUserMetaExample;
import org.toughradius.model.RadUserMetaKey;

public interface RadUserMetaMapper {
    int countByExample(RadUserMetaExample example);

    int deleteByExample(RadUserMetaExample example);

    int deleteByPrimaryKey(RadUserMetaKey key);

    int insert(RadUserMeta record);

    int insertSelective(RadUserMeta record);

    List<RadUserMeta> selectByExampleWithRowbounds(RadUserMetaExample example, RowBounds rowBounds);

    List<RadUserMeta> selectByExample(RadUserMetaExample example);

    RadUserMeta selectByPrimaryKey(RadUserMetaKey key);

    int updateByExampleSelective(@Param("record") RadUserMeta record, @Param("example") RadUserMetaExample example);

    int updateByExample(@Param("record") RadUserMeta record, @Param("example") RadUserMetaExample example);

    int updateByPrimaryKeySelective(RadUserMeta record);

    int updateByPrimaryKey(RadUserMeta record);
}