package org.toughradius.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.toughradius.model.RadUser;
import org.toughradius.model.RadUserExample;

public interface RadUserMapper {
    int countByExample(RadUserExample example);

    int deleteByExample(RadUserExample example);

    int deleteByPrimaryKey(String userName);

    int insert(RadUser record);

    int insertSelective(RadUser record);

    List<RadUser> selectByExampleWithRowbounds(RadUserExample example, RowBounds rowBounds);

    List<RadUser> selectByExample(RadUserExample example);

    RadUser selectByPrimaryKey(String userName);

    int updateByExampleSelective(@Param("record") RadUser record, @Param("example") RadUserExample example);

    int updateByExample(@Param("record") RadUser record, @Param("example") RadUserExample example);

    int updateByPrimaryKeySelective(RadUser record);

    int updateByPrimaryKey(RadUser record);
}