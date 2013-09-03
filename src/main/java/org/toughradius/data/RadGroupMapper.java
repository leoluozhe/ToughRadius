package org.toughradius.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.toughradius.model.RadGroup;
import org.toughradius.model.RadGroupExample;

public interface RadGroupMapper {
    int countByExample(RadGroupExample example);

    int deleteByExample(RadGroupExample example);

    int deleteByPrimaryKey(String groupName);

    int insert(RadGroup record);

    int insertSelective(RadGroup record);

    List<RadGroup> selectByExampleWithRowbounds(RadGroupExample example, RowBounds rowBounds);

    List<RadGroup> selectByExample(RadGroupExample example);

    RadGroup selectByPrimaryKey(String groupName);

    int updateByExampleSelective(@Param("record") RadGroup record, @Param("example") RadGroupExample example);

    int updateByExample(@Param("record") RadGroup record, @Param("example") RadGroupExample example);

    int updateByPrimaryKeySelective(RadGroup record);

    int updateByPrimaryKey(RadGroup record);
}