package org.toughradius.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.toughradius.model.RadClient;
import org.toughradius.model.RadClientExample;

public interface RadClientMapper {
    int countByExample(RadClientExample example);

    int deleteByExample(RadClientExample example);

    int deleteByPrimaryKey(String address);

    int insert(RadClient record);

    int insertSelective(RadClient record);

    List<RadClient> selectByExampleWithRowbounds(RadClientExample example, RowBounds rowBounds);

    List<RadClient> selectByExample(RadClientExample example);

    RadClient selectByPrimaryKey(String address);

    int updateByExampleSelective(@Param("record") RadClient record, @Param("example") RadClientExample example);

    int updateByExample(@Param("record") RadClient record, @Param("example") RadClientExample example);

    int updateByPrimaryKeySelective(RadClient record);

    int updateByPrimaryKey(RadClient record);
}