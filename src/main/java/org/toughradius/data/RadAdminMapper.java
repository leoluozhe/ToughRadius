package org.toughradius.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.toughradius.model.RadAdmin;
import org.toughradius.model.RadAdminExample;

public interface RadAdminMapper {
    int countByExample(RadAdminExample example);

    int deleteByExample(RadAdminExample example);

    int deleteByPrimaryKey(String loginName);

    int insert(RadAdmin record);

    int insertSelective(RadAdmin record);

    List<RadAdmin> selectByExampleWithRowbounds(RadAdminExample example, RowBounds rowBounds);

    List<RadAdmin> selectByExample(RadAdminExample example);

    RadAdmin selectByPrimaryKey(String loginName);

    int updateByExampleSelective(@Param("record") RadAdmin record, @Param("example") RadAdminExample example);

    int updateByExample(@Param("record") RadAdmin record, @Param("example") RadAdminExample example);

    int updateByPrimaryKeySelective(RadAdmin record);

    int updateByPrimaryKey(RadAdmin record);
}